/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.model;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncConfigBean {
    private String ldap_url;
    private String security_principal;
    private String security_password;
    private String groupSearch_filter;
    private String groupMemberSearch_filter;
    private String userMemberSearch_filter;
    private String user_attr;
    private String base_dn;
    private String isNested;

    public String getLdap_url() {
        return ldap_url;
    }

    public void setLdap_url(String ldap_url) {
        this.ldap_url = ldap_url;
    }

    public String getSecurity_principal() {
        return security_principal;
    }

    public void setSecurity_principal(String security_principal) {
        this.security_principal = security_principal;
    }

    public String getSecurity_password() {
        return security_password;
    }

    public void setSecurity_password(String security_password) {
        this.security_password = security_password;
    }

    public String getUser_attr() {
        return user_attr;
    }

    public void setUser_attr(String user_attr) {
        this.user_attr = user_attr;
    }       
    
    public String getBase_dn() {
        return base_dn;
    }

    public void setBase_dn(String base_dn) {
        this.base_dn = base_dn;
    }

    public String getGroupSearch_filter() {
        return groupSearch_filter;
    }

    public void setGroupSearch_filter(String groupSearch_filter) {
        this.groupSearch_filter = groupSearch_filter;
    }

    public String getGroupMemberSearch_filter() {
        return groupMemberSearch_filter;
    }

    public void setGroupMemberSearch_filter(String groupMemberSearch_filter) {
        this.groupMemberSearch_filter = groupMemberSearch_filter;
    }

    public String getUserMemberSearch_filter() {
        return userMemberSearch_filter;
    }

    public void setUserMemberSearch_filter(String userMemberSearch_filter) {
        this.userMemberSearch_filter = userMemberSearch_filter;
    }

    public String getIsNested() {
        return isNested;
    }

    public void setIsNested(String isNested) {
        this.isNested = isNested;
    }   
}
