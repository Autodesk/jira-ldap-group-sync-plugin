/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.ldapgroupsync.plugin.model;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncConfigBean {
    private String ldap_url;
    private String security_principal;
    private String security_password;
    private String search_filter;
    private String member_attr;
    private String user_attr;
    private String base_dn;    

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

    public String getSearch_filter() {
        return search_filter;
    }

    public void setSearch_filter(String search_filter) {
        this.search_filter = search_filter;
    }

    public String getUser_attr() {
        return user_attr;
    }

    public void setUser_attr(String user_attr) {
        this.user_attr = user_attr;
    }        

    public String getMember_attr() {
        return member_attr;
    }

    public void setMember_attr(String member_attr) {
        this.member_attr = member_attr;
    }        
    
    public String getBase_dn() {
        return base_dn;
    }

    public void setBase_dn(String base_dn) {
        this.base_dn = base_dn;
    }        
}
