package com.kwz.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.icesoft.faces.component.tree.IceUserObject;
import com.kwz.entity.KwzBaseBean;
import com.kwz.entity.RootBean;
import com.kwz.entity.User;
import com.kwz.enums.EntityType;
import com.kwz.enums.UserRole;
import com.kwz.faces.NodeUserObject;
import com.kwz.service.KwzDao;
import com.kwz.util.FacesUtils;

@ManagedBean(name = "userEdit")
@SessionScoped
public class UserEdit implements Serializable {

	private static final long serialVersionUID = 8957439343830408210L;
	private static Logger logger = Logger.getLogger(UserEdit.class.getName());

	@ManagedProperty("#{kwzDao}")
	private KwzDao kwzDao;

	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;

	private DefaultTreeModel model;

	private NodeUserObject selected;

    private String inputPassword;
	private String inputPassword2;

	public KwzDao getKwzDao() {
		return kwzDao;
	}

	public void setKwzDao(KwzDao kwzDao) {
		this.kwzDao = kwzDao;
	}

	public String getInputPassword2() {
		return inputPassword2;
	}

	public void setInputPassword2(String inputPassword2) {
		this.inputPassword2 = inputPassword2;
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
		root.setName("All User");
		DefaultMutableTreeNode rootTreeNode = addNode(null, root, -1);
		model = new DefaultTreeModel(rootTreeNode);
		for (User n : kwzDao.getUsers()) {
			if (canEdit(n))
				addNode(rootTreeNode, n, -1);
		}
	}

	private boolean canEdit(User n) {
		return loginBean.hasRole(UserRole.ROLE_USER_ADMIN.name()) || loginBean.isLoggedIn() && n.getName().equals(loginBean.getLogin());
	}

	public String nodeSelected(String id) {
		// get employee id
//		String id = FacesUtils.getRequestParameter("userObjectId");
		if (StringUtils.isBlank(id))
			selected = null;
		DefaultMutableTreeNode node = getTreeNode(id);
		if (node != null) { // shouldn't happen
			selected = (NodeUserObject) node.getUserObject();

			NodeUserObject nodeUserObject = (NodeUserObject) node.getUserObject();
			nodeUserObject.setExpanded(!nodeUserObject.isExpanded());
		} else
			selected = null;
		return "home.xhtml";
	}

	public void nodeDelete(ActionEvent event) {
		if (selected != null) {
			Object o = selected.getData();
			DefaultMutableTreeNode node = selected.getWrapper();
			if (o instanceof User) {
				if (!canEdit((User) o))
					return;
				kwzDao.getUsers().remove(o);
				// kwzDao.removeUser((User)o);
			}
			selected.getWrapper().removeFromParent();
			selected = null;
			kwzDao.setChanged(EntityType.User);
		}
	}

	public void nodeSave(ActionEvent event) {
		if (selected != null) {
			Object o = selected.getData();
			DefaultMutableTreeNode node = selected.getWrapper();
			if (o instanceof User) {
				if (!canEdit((User) o))
					return;
				if (!StringUtils.isBlank(inputPassword) && !StringUtils.isBlank(inputPassword2))
					if (inputPassword2.equals(inputPassword))
						((User) selected.getData()).digestPassword(inputPassword);
					else {
						FacesUtils.addErrorMessage(":userModify:inputPassword", "Not match");
						return;
					}
				kwzDao.setChanged(EntityType.User);
//				BeanUtils.copyProperties(selectedData, selected.getData());
			}
		}

	}

	public void newUser(ActionEvent event) {
		DefaultMutableTreeNode node = null;
		node = (DefaultMutableTreeNode) model.getRoot();
		User user = kwzDao.newUser();
        user.digestPassword();
		node = addNode(node, user, 0);
		kwzDao.getUsers().add(0, user);
		selected = (NodeUserObject) node.getUserObject();
//        selectedData = (KwzBaseBean) selected.getData();
//        selectedData = (KwzBaseBean) SerializationUtils.deserialize(SerializationUtils.serialize(selectedData));
		kwzDao.setChanged(EntityType.User);
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

	private Object getData(DefaultMutableTreeNode dragNode) {
		NodeUserObject nodeUserObject = (NodeUserObject) dragNode.getUserObject();
		return nodeUserObject.getData();
	}

	public String getInputPassword() {
		return inputPassword;
	}

	public void setInputPassword(String inputPassword) {
		this.inputPassword = inputPassword;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

}
