package com.adsk.jira.ldapgroupsync.plugin.config;

import com.adsk.jira.ldapgroupsync.plugin.ao.LdapGroupSyncMap;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;


/**
 * @author scmenthusiast@gmail.com
 */
public abstract interface LdapGroupSyncMapMgr {
    public abstract LdapGroupSyncMap[] getGroupsMapProperties();
    public abstract void setGroupsMapProperty(LdapGroupSyncMapBean configBean);
    public abstract boolean findGroupsMapProperty(LdapGroupSyncMapBean configBean);
    public abstract void removeGroupsMapProperty(long mapId);
}
