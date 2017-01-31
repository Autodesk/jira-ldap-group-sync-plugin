/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.ldapgroupsync.plugin.config;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tse.jira.ldapgroupsync.plugin.model.LdapGroupSyncConfigBean;

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
        configBean.setSearch_filter(applicationProperties.getString(LdapGroupSyncConfigMgr.SEARCH_FILTER));
        configBean.setBase_dn(applicationProperties.getString(LdapGroupSyncConfigMgr.BASE_DN));
        configBean.setMember_attr(applicationProperties.getString(LdapGroupSyncConfigMgr.MEMBER_ATTR));
        configBean.setUser_attr(applicationProperties.getString(LdapGroupSyncConfigMgr.USER_ATTR));
        return configBean;
    }

    @Override
    public void setGroupsConfigProperties(LdapGroupSyncConfigBean configBean) {
        applicationProperties.setString(LdapGroupSyncConfigMgr.LDAP_URL, configBean.getLdap_url());
        applicationProperties.setString(LdapGroupSyncConfigMgr.SECURITY_PRINCIPAL, configBean.getSecurity_principal());
        applicationProperties.setString(LdapGroupSyncConfigMgr.SECURITY_PASSWORD, configBean.getSecurity_password());
        applicationProperties.setString(LdapGroupSyncConfigMgr.SEARCH_FILTER, configBean.getSearch_filter());
        applicationProperties.setString(LdapGroupSyncConfigMgr.BASE_DN, configBean.getBase_dn());
        applicationProperties.setString(LdapGroupSyncConfigMgr.MEMBER_ATTR, configBean.getMember_attr());
        applicationProperties.setString(LdapGroupSyncConfigMgr.USER_ATTR, configBean.getUser_attr());
    }
    
}
