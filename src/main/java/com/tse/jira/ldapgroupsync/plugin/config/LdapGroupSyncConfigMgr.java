/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.ldapgroupsync.plugin.config;

import com.tse.jira.ldapgroupsync.plugin.model.LdapGroupSyncConfigBean;

/**
 *
 * @author prasadve
 */
public abstract interface LdapGroupSyncConfigMgr {
    public static final String LDAP_URL = "com.tse.jira.ldapgroupsync.plugin.ldap_url";
    public static final String SECURITY_PRINCIPAL = "com.tse.jira.ldapgroupsync.plugin.security_principal";
    public static final String SECURITY_PASSWORD = "com.tse.jira.ldapgroupsync.plugin.security_password";
    public static final String SEARCH_FILTER = "com.tse.jira.ldapgroupsync.plugin.search_filter";
    public static final String BASE_DN = "com.tse.jira.ldapgroupsync.plugin.base_dn";
    public static final String MEMBER_ATTR = "com.tse.jira.ldapgroupsync.plugin.member_attr";
    public static final String USER_ATTR = "com.tse.jira.ldapgroupsync.plugin.user_attr";
    public abstract LdapGroupSyncConfigBean getGroupsConfigProperties();
    public abstract void setGroupsConfigProperties(LdapGroupSyncConfigBean configBean);
}
