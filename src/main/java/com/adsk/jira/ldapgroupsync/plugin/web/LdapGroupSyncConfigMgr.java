/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.web;

import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncConfigBean;

/**
 *
 * @author prasadve
 */
public abstract interface LdapGroupSyncConfigMgr {
    public static final String LDAP_URL = "com.adsk.jira.ldapgroupsync.plugin.ldap_url";
    public static final String SECURITY_PRINCIPAL = "com.adsk.jira.ldapgroupsync.plugin.security_principal";
    public static final String SECURITY_PASSWORD = "com.adsk.jira.ldapgroupsync.plugin.security_password";
    public static final String USER_MEMBER_SEARCH_FILTER = "com.adsk.jira.ldapgroupsync.plugin.user_member_search_filter";
    public static final String GROUP_SEARCH_FILTER = "com.adsk.jira.ldapgroupsync.plugin.group_search_filter";
    public static final String GROUP_MEMBER_SEARCH_FILTER = "com.adsk.jira.ldapgroupsync.plugin.group_member_search_filter";
    public static final String BASE_DN = "com.adsk.jira.ldapgroupsync.plugin.base_dn";
    public static final String USER_ATTR = "com.adsk.jira.ldapgroupsync.plugin.user_attr";
    public static final String IS_NESTED = "com.adsk.jira.ldapgroupsync.plugin.is_nested";
    public abstract LdapGroupSyncConfigBean getGroupsConfigProperties();
    public abstract void setGroupsConfigProperties(LdapGroupSyncConfigBean configBean);
}
