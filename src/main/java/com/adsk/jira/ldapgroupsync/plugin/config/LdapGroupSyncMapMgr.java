package com.adsk.jira.ldapgroupsync.plugin.config;

import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;


/**
 * @author scmenthusiast@gmail.com
 */
public abstract interface LdapGroupSyncMapMgr {
    public static final String GROUPS_MAP = "com.adsk.jira.ldapgroupsync.plugin.groups_map";
    public abstract LdapGroupSyncMapBean getGroupsMapProperties();
    public abstract void setGroupsMapProperties(LdapGroupSyncMapBean configBean);
}
