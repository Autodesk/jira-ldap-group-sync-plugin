package com.tse.jira.ldapgroupsync.plugin.config;

import com.tse.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncMapMgrImpl implements LdapGroupSyncMapMgr {
    
    private final ApplicationProperties applicationProperties;
    
    public LdapGroupSyncMapMgrImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public LdapGroupSyncMapBean getGroupsMapProperties() {
        LdapGroupSyncMapBean configBean = new LdapGroupSyncMapBean();
        configBean.setGroups_map(applicationProperties.getString(LdapGroupSyncMapMgr.GROUPS_MAP));
        return configBean;
    }

    @Override
    public void setGroupsMapProperties(LdapGroupSyncMapBean configBean) {
        applicationProperties.setString(LdapGroupSyncMapMgr.GROUPS_MAP, configBean.getGroups_map());
    }
    
}
