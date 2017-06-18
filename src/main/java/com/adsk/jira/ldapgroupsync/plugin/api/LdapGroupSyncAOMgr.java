/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.api;

import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import java.util.List;

/**
 *
 * @author prasadve
 */
public interface LdapGroupSyncAOMgr {
    public ActiveObjects getActiveObjects();
    public List<LdapGroupSyncMapBean> getAllGroupsMapProperties();
    public List<LdapGroupSyncMapBean> getSupportedGroupsMapProperties();
    public LdapGroupSyncMapBean getGroupsMapProperty(long configId);
    public void addGroupsMapProperty(LdapGroupSyncMapBean configBean);
    public void setGroupsMapProperty(LdapGroupSyncMapBean configBean);
    public boolean findGroupsMapProperty(LdapGroupSyncMapBean configBean);
    public boolean findGroupsMapProperty2(LdapGroupSyncMapBean configBean);
    public boolean isJiraGroupNotInSupport(String jiraGroup);
    public void removeGroupsMapProperty(long configId);
}
