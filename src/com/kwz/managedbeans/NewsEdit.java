package com.kwz.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.tree.IceUserObject;
import com.kwz.entity.KwzBaseBean;
import com.kwz.entity.News;
import com.kwz.entity.RootBean;
import com.kwz.enums.EntityType;
import com.kwz.faces.NodeUserObject;
import com.kwz.service.KwzDao;
import com.kwz.util.FacesUtils;

@ManagedBean(name = "newsEdit")
@SessionScoped
public class NewsEdit implements Serializable {

    private static final long serialVersionUID = 8957439343830408210L;
    private static Logger logger = Logger.getLogger(NewsEdit.class.getName());

    @ManagedProperty("#{kwzDao}")
    private KwzDao kwzDao;

    @ManagedProperty("#{loginBean}")
    private LoginBean loginBean;

    private DefaultTreeModel model;

    private NodeUserObject selected;

    public KwzDao getKwzDao() {
        return kwzDao;
    }

    public void setKwzDao(KwzDao kwzDao) {
        this.kwzDao = kwzDao;
    }

    public NodeUserObject getSelected() {
        return selected;
    }

    private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent, KwzBaseBean data, int index) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        NodeUserObject userObject = new NodeUserObject(node);
        node.setUserObject(userObject);
        userObject.setData(data);

        userObject.setText(data.getName());
        // Top level
        if (parent == null) {
            userObject.setExpanded(true);
            userObject.setLeaf(false);
            node.setAllowsChildren(true);
        }
        // product node
        else {
            userObject.setLeaf(true);
            node.setAllowsChildren(false);
        }

        // finally add the node to the parent.
        if (parent != null) {
            if (index == -1)
                parent.add(node);
            else
                parent.insert(node, index);
        }
        return node;
    }

    public DefaultTreeModel getModel() {
        return model;
    }

    @PostConstruct
    public void initModel() {
        RootBean root = new RootBean();
        root.setName("All News");
        DefaultMutableTreeNode rootTreeNode = addNode(null, root, -1);
        model = new DefaultTreeModel(rootTreeNode);
        for (News n : kwzDao.getNews()) {
            DefaultMutableTreeNode nNode = addNode(rootTreeNode, n, -1);
        }
    }

    public void nodeSelected(String id) {
        // get employee id
        // String id = FacesUtils.getRequestParameter("userObjectId");
        if (StringUtils.isBlank(id))
            selected = null;
        DefaultMutableTreeNode node = getTreeNode(id);
        if (node != null) { // shouldn't happen
            selected = (NodeUserObject) node.getUserObject();
            NodeUserObject nodeUserObject = (NodeUserObject) node.getUserObject();
            nodeUserObject.setExpanded(!nodeUserObject.isExpanded());
        } else {
            selected = null;
        }
    }

    @RolesAllowed("ROLE_ADMIN")
    public void nodeDelete(ActionEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;
        if (selected != null) {
            Object o = selected.getData();
            DefaultMutableTreeNode node = selected.getWrapper();
            if (o instanceof News) {
                kwzDao.getNews().remove(o);
                // kwzDao.removeNews((News)o);
            }

            DefaultMutableTreeNode nextNode = selected.getWrapper().getNextNode();
            if (nextNode == null)
                nextNode = selected.getWrapper().getPreviousNode();
            selected.getWrapper().removeFromParent();
            if (nextNode != null && nextNode.getUserObject() != null)
                selected = (NodeUserObject) nextNode.getUserObject();
            else
                selected = null;
            kwzDao.setChanged(EntityType.News);
        }
    }

    @RolesAllowed("ROLE_ADMIN")
    public void nodeSave(ActionEvent event) {
        kwzDao.setChanged(EntityType.News);
    }

    @RolesAllowed("ROLE_ADMIN")
    public void newNews(ActionEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;
        DefaultMutableTreeNode node = null;
        node = (DefaultMutableTreeNode) model.getRoot();
        News news = kwzDao.newNews();
        news.setAuthor(loginBean.getLogin());
        news.setContentUpdated(new Date());
        node = addNode(node, news, 0);
        kwzDao.getNews().add(0, news);
        selected = (NodeUserObject) node.getUserObject();
        kwzDao.setChanged(EntityType.News);
    }

    private DefaultMutableTreeNode getTreeNode(String id) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode node;
        NodeUserObject ret;
        Enumeration nodes = rootNode.depthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            node = (DefaultMutableTreeNode) nodes.nextElement();
            ret = (NodeUserObject) node.getUserObject();
            if (ret.getData() != null) {
                KwzBaseBean o = (KwzBaseBean) ret.getData();
                if (id.equals(o.getId()))
                    return node;
            }
        }
        return null;
    }

    public ArrayList getSelectedTreePath() {
        Object[] objectPath = selected.getWrapper().getUserObjectPath();
        ArrayList treePath = new ArrayList();
        Object anObjectPath;
        for (int i = 0, max = objectPath.length; i < max; i++) {
            anObjectPath = objectPath[i];
            IceUserObject userObject = (IceUserObject) anObjectPath;
            treePath.add(userObject.getText());
        }
        return treePath;
    }

    @RolesAllowed("ROLE_ADMIN")
    public void dropListener(DropEvent event) {
        logger.info("Start drop");
        HtmlPanelGroup panelGroup = (HtmlPanelGroup) event.getComponent();

        DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) event.getTargetDragValue();
        Object dragData = getData(dragNode);
        // can drag a product only
        if (!(dragData instanceof KwzBaseBean))
            return;
        if (StringUtils.isBlank(((KwzBaseBean) dragData).getId()))
            return;
        DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) panelGroup.getDropValue();

        if (dragNode.isNodeDescendant(dropNode))
            return;

        DefaultMutableTreeNode dragNodeParent = (DefaultMutableTreeNode) dragNode.getParent();

        DefaultMutableTreeNode dropNodeParent = (DefaultMutableTreeNode) dropNode.getParent();

        Object dropData = getData(dropNode);
        if (!(dropData instanceof KwzBaseBean))
            return;
        // Root node?
        if (dragData == null)
            return;
        if (dragData instanceof News) { // Moving a news
            // remove the product from the orginal catalog
            News dragNews = (News) dragData;
            kwzDao.getNews().remove(dragNews);

            dragNode.removeFromParent();
            if (dropNode.getAllowsChildren()) {// drop to a root
                dropNode.insert(dragNode, 0);
                kwzDao.getNews().add(0, dragNews);
            } else { // drop to a News
                kwzDao.getNews().add(dropNodeParent.getIndex(dropNode), dragNews);
                dropNodeParent.insert(dragNode, dropNodeParent.getIndex(dropNode));
            }
            kwzDao.setChanged(EntityType.News);
        }
    }

    private Object getData(DefaultMutableTreeNode dragNode) {
        NodeUserObject nodeUserObject = (NodeUserObject) dragNode.getUserObject();
        return nodeUserObject.getData();
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

}
