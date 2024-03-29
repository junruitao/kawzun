/*
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2011 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 */

package com.kwz.faces;

import javax.swing.tree.DefaultMutableTreeNode;

import com.icesoft.faces.component.tree.IceUserObject;
import com.kwz.entity.Catalog;
import com.kwz.entity.Product;

public class NodeUserObject extends IceUserObject {

	private Object data;
    public NodeUserObject(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);

        setLeafIcon("tree_document.gif");
        setBranchContractedIcon("tree_folder_closed.gif");
        setBranchExpandedIcon("tree_folder_open.gif");
    }
    
//    public String getText(){
//    	if (data == null)
//    		return "ROOT";
//    	if (data instanceof Catalog)
//    		return ((Catalog) data).getName();
//    	if (data instanceof Product)
//    		return ((Product) data).getName();
//   		return "N/A";
//    }
    
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

}
