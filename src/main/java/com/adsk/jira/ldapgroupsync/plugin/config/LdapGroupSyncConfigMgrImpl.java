/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.config;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncConfigBean;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncConfigMgrImpl implements LdapGroupSyncConfigMgr {

    private final ApplicationProperties applicationProperties;
    
    public LdapGroupSyncConfigMgrImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public LdapGroupSyncConfigBean getGroupsConfigProperties() {
        LdapGroupSyncConfigBean configBean = new LdapGroupSyncConfigBean();
        configBean.setLdap_url(applicationProperties.getString(LdapGroupSyncConfigMgr.LDAP_URL));
        configBean.setSecurity_principal(applicationProperties.getString(LdapGroupSyncConfigMgr.SECURITY_PRINCIPAL));
        configBean.setSecurity_password(applicationProperties.getString(LdapGroupSyncConfigMgr.SECURITY_PASSWORD));
        configBean.setUserMemberSearch_filter(applicationProperties.getString(LdapGroupSyncConfigMgr.USER_MEMBER_SEARCH_FILTER));
        configBean.setGroupSearch_filter(applicationProperties.getString(LdapGroupSyncConfigMgr.GROUP_SEARCH_FILTER));
        configBean.setGroupMemberSearch_filter(applicationProperties.getString(LdapGroupSyncConfigMgr.GROUP_MEMBER_SEARCH_FILTER));
        configBean.setBase_dn(applicationProperties.getString(LdapGroupSyncConfigMgr.BASE_DN));
        configBean.setUser_attr(applicationProperties.getString(LdapGroupSyncConfigMgr.USER_ATTR));
        configBean.setIsNested(applicationProperties.getString(LdapGroupSyncConfigMgr.IS_NESTED));
        return configBean;
    }

    @Override
    public void setGroupsConfigProperties(LdapGroupSyncConfigBean configBean) {
        applicationProperties.setString(LdapGroupSyncConfigMgr.LDAP_URL, configBean.getLdap_url());
        applicationProperties.setString(LdapGroupSyncConfigMgr.SECURITY_PRINCIPAL, configBean.getSecurity_principal());
        applicationProperties.setString(LdapGroupSyncConfigMgr.SECURITY_PASSWORD, configBean.getSecurity_password());
        applicationProperties.setString(LdapGroupSyncConfigMgr.GROUP_SEARCH_FILTER, configBean.getGroupSearch_filter());
        applicationProperties.setString(LdapGroupSyncConfigMgr.GROUP_MEMBER_SEARCH_FILTER, configBean.getGroupMemberSearch_filter());
        applicationProperties.setString(LdapGroupSyncConfigMgr.USER_MEMBER_SEARCH_FILTER, configBean.getUserMemberSearch_filter());
        applicationProperties.setString(LdapGroupSyncConfigMgr.BASE_DN, configBean.getBase_dn());
        applicationProperties.setString(LdapGroupSyncConfigMgr.USER_ATTR, configBean.getUser_attr());
        applicationProperties.setString(LdapGroupSyncConfigMgr.IS_NESTED, configBean.getIsNested());
    }
    
}
