package com.adsk.jira.ldapgroupsync.plugin.config;

import com.adsk.jira.ldapgroupsync.plugin.ao.LdapGroupSyncMap;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import static com.google.common.base.Preconditions.checkNotNull;
import net.java.ao.Query;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncMapMgrImpl implements LdapGroupSyncMapMgr {
    
    private final ActiveObjects ao;
    
    public LdapGroupSyncMapMgrImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }
    
    @Override
    public LdapGroupSyncMap[] getGroupsMapProperties() {        
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select());
        return maps;
    }

    @Override
    public void setGroupsMapProperty(LdapGroupSyncBean configBean) {
        final LdapGroupSyncMap map = ao.create(LdapGroupSyncMap.class);
        map.setLdapGroup(configBean.getLdapGroup());
        map.setJiraGroup(configBean.getJiraGroup());
        map.save();
    }
    
    @Override
    public boolean findGroupsMapProperty(LdapGroupSyncBean configBean) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select().where("LDAP_GROUP = ? OR JIRA_GROUP = ?", 
                configBean.getLdapGroup(), configBean.getJiraGroup()));
        if(maps.length > 0) {
            return true;
        }
        return false;
    }

    public void removeGroupsMapProperty(long mapId) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select().where("ID = ?", mapId));
        if(maps.length > 0) {
            ao.delete(maps[0]);
        }
    }
    
}
