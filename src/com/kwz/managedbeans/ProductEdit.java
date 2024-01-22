package com.kwz.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.kwz.entity.Catalog;
import com.kwz.entity.KwzBaseBean;
import com.kwz.entity.Product;
import com.kwz.entity.RootBean;
import com.kwz.enums.EntityType;
import com.kwz.faces.NodeUserObject;
import com.kwz.service.KwzDao;
import com.kwz.util.FacesUtils;

@ManagedBean(name = "productEdit")
@SessionScoped
@RolesAllowed("ROLE_ADMIN")
public class ProductEdit implements Serializable {

    private static final long serialVersionUID = 8957439343830408210L;
    private static Logger logger = Logger.getLogger(ProductEdit.class.getName());

    @ManagedProperty("#{kwzDao}")
    private KwzDao kwzDao;

    @ManagedProperty("#{loginBean}")
    private LoginBean loginBean;
    
    private DefaultTreeModel model;

    private NodeUserObject selected;

    // loads catalogs & products

    public void setKwzDao(KwzDao kwzDao) {
        this.kwzDao = kwzDao;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public NodeUserObject getSelected() {
        return selected;
    }

    private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent, KwzBaseBean data) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        NodeUserObject userObject = new NodeUserObject(node);
        node.setUserObject(userObject);
        userObject.setData(data);

        // non-Product node
        if (data == null)
            return null;
        if (data instanceof Catalog || data instanceof RootBean) {
            if (parent == null)
                userObject.setExpanded(true);
            if (data instanceof Catalog)
                userObject.setText(((Catalog) data).getName());
            else
                userObject.setText("Root");
            userObject.setLeaf(false);
            node.setAllowsChildren(true);
        }
        // product node
        else {
            userObject.setText(((Product) data).getName());
            userObject.setLeaf(true);
            node.setAllowsChildren(false);
        }

        // finally add the node to the parent.
        if (parent != null) {
            parent.add(node);
        }
        return node;
    }

    public DefaultTreeModel getModel() {
        return model;
    }

    @PostConstruct
    public void initModel() {
        RootBean rootCat = new RootBean();
        // rootCat.setName("All Catalog");
        DefaultMutableTreeNode rootTreeNode = addNode(null, rootCat);
        model = new DefaultTreeModel(rootTreeNode);
        for (Catalog cat : kwzDao.getCatalogs()) {
            DefaultMutableTreeNode catNode = addNode(rootTreeNode, cat);
            for (Product p : cat.getProducts()) {
                addNode(catNode, p);
            }
        }
    }

    public void nodeSelected(String id,String type){
        // get employee id
//        String id = FacesUtils.getRequestParameter("userObjectId");
//        String type = FacesUtils.getRequestParameter("type");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(type))
            selected = null;
        DefaultMutableTreeNode node = getTreeNode(type, id);
        if (node != null) { // shouldn't happen
            selected = (NodeUserObject) node.getUserObject();
            NodeUserObject nodeUserObject = (NodeUserObject) node.getUserObject();
            nodeUserObject.setExpanded(!nodeUserObject.isExpanded());
        } else
            selected = null;
    }

    public void nodeDelete(ActionEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;
        if (selected != null) {
            Object o = selected.getData();
            DefaultMutableTreeNode node = selected.getWrapper();
            if (o instanceof Product) {
                Catalog catalog = (Catalog) getData((DefaultMutableTreeNode) node.getParent());
                catalog.getProducts().remove(o);
            } else {
                kwzDao.getCatalogs().remove(o);
            }
            DefaultMutableTreeNode nextNode = selected.getWrapper().getNextNode();
            if (nextNode == null)
                nextNode = selected.getWrapper().getPreviousNode();
            selected.getWrapper().removeFromParent();
            if (nextNode != null && nextNode.getUserObject() != null)
                selected = (NodeUserObject) nextNode.getUserObject();
            else
                selected = null;
            kwzDao.setChanged(EntityType.Catalog);
        }
    }

    public void nodeSave(ActionEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;
        if (selected != null) {
            Object o = selected.getData();
            DefaultMutableTreeNode node = selected.getWrapper();
            if (o instanceof Product || o instanceof Catalog) {
                kwzDao.setChanged(EntityType.Catalog);
            }
        }
    }

    public void newProduct(ActionEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;
        if (selected != null) {
            DefaultMutableTreeNode node = selected.getWrapper();
            if (selected.getData() instanceof Product)
                node = (DefaultMutableTreeNode) node.getParent();
            NodeUserObject o = (NodeUserObject) node.getUserObject();
            Object cat = o.getData();
            if (cat instanceof Catalog) {
                Catalog c = (Catalog) cat;
                Product p = kwzDao.newProduct();
                node = addNode(node, p);
                selected = (NodeUserObject) node.getUserObject();
                // kwzDao.addProduct(c, p);
                ((Catalog) cat).getProducts().add(p);
                kwzDao.setChanged(EntityType.Catalog);
            }
        }
    }

    public void newCatalog(ActionEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        Catalog c = kwzDao.newCatalog();
        kwzDao.getCatalogs().add(c);
        kwzDao.setChanged(EntityType.Catalog);
        DefaultMutableTreeNode node = addNode(rootNode, c);
        selected = (NodeUserObject) node.getUserObject();
    }

    private DefaultMutableTreeNode getTreeNode(String type, String id) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode node;
        NodeUserObject ret;
        Enumeration nodes = rootNode.depthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            node = (DefaultMutableTreeNode) nodes.nextElement();
            ret = (NodeUserObject) node.getUserObject();
            if (ret.getData() != null) {
                KwzBaseBean o = (KwzBaseBean) ret.getData();
                if (o.getClass().getSimpleName().equals(type) && id.equals(o.getId()))
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

    public void dropListener(DropEvent event) {
        if (!loginBean.hasRole("ROLE_ADMIN"))
            return;
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
        if (dragData instanceof Product) { // Moving a product
            // cannot move a product to root
            if (StringUtils.isBlank(((KwzBaseBean) dropData).getId()))
                return;
            // remove the product from the orginal catalog
            Product dragProduct = (Product) dragData;
            Catalog dragFromCatalog = (Catalog) getData(dragNodeParent);
            dragFromCatalog.getProducts().remove(dragProduct);

            if (dropNode.getAllowsChildren()) {// drop to a catalog
                dropNode.insert(dragNode, 0);
                Catalog dropToCatalog = (Catalog) getData(dropNode);
                dropToCatalog.getProducts().add(0, dragProduct);
            } else { // drop to a product
                dragNode.removeFromParent();
                Catalog dropToCatalog = (Catalog) getData(dropNodeParent);
                dropToCatalog.getProducts().add(dropNodeParent.getIndex(dropNode), dragProduct);
                dropNodeParent.insert(dragNode, dropNodeParent.getIndex(dropNode));
            }
        } else { // moving a catalog
            Catalog dragCatalog = (Catalog) dragData;
            int index = 0;
            // Find a node and insert
            if (dropNodeParent != null) {
                while (dropNodeParent.getParent() != null) { // Moving to a product
                    dropNode = dropNodeParent;
                    dropNodeParent = (DefaultMutableTreeNode) dropNode.getParent();
                }
                index = dropNodeParent.getIndex(dropNode);
                dropNodeParent.insert(dragNode, index);
            } else
                dropNode.insert(dragNode, 0);
            kwzDao.getCatalogs().remove(dragCatalog);
            kwzDao.getCatalogs().add(index, dragCatalog);
        }
        kwzDao.setChanged(EntityType.Catalog);
    }

    private Object getData(DefaultMutableTreeNode dragNode) {
        NodeUserObject nodeUserObject = (NodeUserObject) dragNode.getUserObject();
        return nodeUserObject.getData();
    }

}
