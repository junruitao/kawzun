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

package com.kwz.navigation;

import javax.faces.event.ActionEvent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * The NavigationBean class is responsible for storing the state of the included dynamic content for display.
 * </p>
 * 
 * @since 0.3.0
 */
@ManagedBean(name = "navigation")
@SessionScoped
public class NavigationBean implements Serializable {

    private static final String HomePath = "/WEB-INF/includes/content/index.xhtml";
    private static final long serialVersionUID = 1L;
    // selected include contents.
    private String selectedIncludePath = HomePath;

    /**
     * Gets the currently selected content include path.
     * 
     * @return currently selected content include path.
     */
    public String getSelectedIncludePath() {
        // check for a currently selected path to be ready for ui:include
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        String requestedPath = (String) map.get("includePath");
        if ((null != requestedPath) && (requestedPath.length() > 0)) {
            selectedIncludePath = requestedPath;
        } else if (StringUtils.isBlank(selectedIncludePath))
            selectedIncludePath = HomePath;

        return selectedIncludePath;
    }

    /**
     * Sets the selected content include path to the specified path.
     * 
     * @param selectedIncludePath
     *            the specified content include path.
     */
    public void setSelectedIncludePath(String selectedIncludePath) {
        this.selectedIncludePath = selectedIncludePath;
    }

    /**
     * Action Listener for the changes the selected content path in the facelets version of component showcase.
     * 
     * @param event
     *            JSF Action Event.
     */
    public void navigationPathChange(ActionEvent event) {

        // get from the context content include path to show as well
        // as the title associated with the link.
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        selectedIncludePath = (String) map.get("includePath");
    }

    public void reset() {
        selectedIncludePath = HomePath;
    }

    public boolean isHome() {
        return StringUtils.isBlank(selectedIncludePath)||selectedIncludePath.equals(HomePath);
    }

}