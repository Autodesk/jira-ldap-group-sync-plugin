package com.adsk.jira.ldapgroupsync.plugin.config;

import com.adsk.jira.ldapgroupsync.plugin.ao.LdapGroupSyncMap;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncBean;


/**
 * @author scmenthusiast@gmail.com
 */
public abstract interface LdapGroupSyncMapMgr {
    public static final String GROUPS_MAP = "com.adsk.jira.ldapgroupsync.plugin.groups_map";
    /*public abstract LdapGroupSyncMapBean getGroupsMapProperties();
    public abstract void setGroupsMapProperties(LdapGroupSyncMapBean configBean);*/
    public abstract LdapGroupSyncMap[] getGroupsMapProperties();
    public abstract void setGroupsMapProperty(LdapGroupSyncBean configBean);
    public abstract boolean findGroupsMapProperty(LdapGroupSyncBean configBean);
    public abstract void removeGroupsMapProperty(long mapId);
}
