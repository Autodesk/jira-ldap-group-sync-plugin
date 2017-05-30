/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.web;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncConfigBean;
import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncLDAPUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncConfigAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncConfigAction.class);
    private LdapGroupSyncConfigBean configBean = new LdapGroupSyncConfigBean();
    private final LdapGroupSyncConfigMgr ldapGroupSyncConfigMgr;
    private String submitted;
    private String status;
    
    public LdapGroupSyncConfigAction(LdapGroupSyncConfigMgr ldapGroupSyncConfigMgr) {
        this.ldapGroupSyncConfigMgr = ldapGroupSyncConfigMgr;
    }
    
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        
        if (this.submitted == null) {
            //ldapGroupSyncConfigMgr.setGroupsConfigProperties(configBean); //to clear Config
            configBean = ldapGroupSyncConfigMgr.getGroupsConfigProperties();            
        } else {
            
            String LDAP_URL = configBean.getLdap_url();
            String SECURITY_PRINCIPAL = configBean.getSecurity_principal();
            String SECURITY_PASSWORD = configBean.getSecurity_password();
            String GROUP_SEARCH_FILTER = configBean.getGroupSearch_filter();
            String GROUP_MEMBER_SEARCH_FILTER = configBean.getGroupMemberSearch_filter();
            String USER_MEMBER_SEARCH_FILTER = configBean.getUserMemberSearch_filter();
            String BASE_DN = configBean.getBase_dn();
            String USER_ATTR = configBean.getUser_attr();
            String IS_NESTED = configBean.getIsNested();
            
            LOGGER.debug("LDAP URL -> "+ LDAP_URL);
            LOGGER.debug("SECURITY PRINCIPAL -> "+ SECURITY_PRINCIPAL);
            LOGGER.debug("GROUP SEARCH FILTER -> "+ GROUP_SEARCH_FILTER);
            LOGGER.debug("GROUP MEMBER SEARCH FILTER -> "+ GROUP_MEMBER_SEARCH_FILTER);
            LOGGER.debug("BASE DN -> "+ BASE_DN);
            LOGGER.debug("USER MEMBER SEARCH FILTER -> "+ USER_MEMBER_SEARCH_FILTER);
            LOGGER.debug("USER ATTR -> "+ USER_ATTR);
            LOGGER.debug("IS NESTED -> "+ IS_NESTED);
            
            if( LDAP_URL != null && !"".equals(LDAP_URL) && SECURITY_PRINCIPAL != null && !"".equals(SECURITY_PRINCIPAL) 
                    && SECURITY_PASSWORD != null && !"".equals(SECURITY_PASSWORD) && BASE_DN != null && !"".equals(BASE_DN) ) {
                ldapGroupSyncConfigMgr.setGroupsConfigProperties(configBean);
                
                LdapGroupSyncLDAPUtils.LDAP_URL = configBean.getLdap_url();
                LdapGroupSyncLDAPUtils.SECURITY_PRINCIPAL = configBean.getSecurity_principal();
                LdapGroupSyncLDAPUtils.SECURITY_PASSWORD = configBean.getSecurity_password();
                LdapGroupSyncLDAPUtils.BASE_DN = configBean.getBase_dn();
                
                if(USER_MEMBER_SEARCH_FILTER != null && !"".equals(USER_MEMBER_SEARCH_FILTER)) {
                    LdapGroupSyncLDAPUtils.USER_MEMBER_SEARCH_FILTER = configBean.getUserMemberSearch_filter();
                } else {
                    LdapGroupSyncLDAPUtils.USER_MEMBER_SEARCH_FILTER = "(&(objectClass=user)(memberOf={0}))"; //default
                }
                
                if(GROUP_SEARCH_FILTER != null && !"".equals(GROUP_SEARCH_FILTER)) { 
                    LdapGroupSyncLDAPUtils.GROUP_SEARCH_FILTER = configBean.getGroupSearch_filter();
                } else {
                    LdapGroupSyncLDAPUtils.GROUP_SEARCH_FILTER = "(&(objectClass=group)(sAMAccountName={0}))"; //default
                }
                
                if(GROUP_MEMBER_SEARCH_FILTER != null && !"".equals(GROUP_MEMBER_SEARCH_FILTER)) { 
                    LdapGroupSyncLDAPUtils.GROUP_MEMBER_SEARCH_FILTER = configBean.getGroupMemberSearch_filter();
                } else {
                    LdapGroupSyncLDAPUtils.GROUP_MEMBER_SEARCH_FILTER = "(&(objectClass=group)(memberOf={0}))"; //default
                }
                
                if(USER_ATTR != null && !"".equals(USER_ATTR)) {
                    LdapGroupSyncLDAPUtils.USER_ATTR = configBean.getUser_attr();
                } else {
                    LdapGroupSyncLDAPUtils.USER_ATTR = "sAMAccountName"; //default
                }
                
                if(IS_NESTED != null && !"".equals(IS_NESTED)) {
                    LdapGroupSyncLDAPUtils.IS_NESTED = configBean.getIsNested();
                } else {
                    LdapGroupSyncLDAPUtils.IS_NESTED = "FALSE"; //default
                }
                
                LdapGroupSyncLDAPUtils.destroyLdapContext(); //to pick latest config
                status = "Saved.";
            } else {
                status = "Failed. Required fields are missing!";
            }                                    
        }
        
        return "success";
    }
    
    public String getLdap_url() {
        return configBean.getLdap_url();
    }

    public void setLdap_url(String ldap_url) {
        configBean.setLdap_url(ldap_url);
    }

    public String getSecurity_principal() {
        return configBean.getSecurity_principal();
    }

    public void setSecurity_principal(String security_principal) {
        configBean.setSecurity_principal(security_principal);
    }

    public String getSecurity_password() {
        return configBean.getSecurity_password();
    }
    
    public String getUserMemberSearch_filter() {
        return configBean.getUserMemberSearch_filter();
    }

    public void setUserMemberSearch_filter(String search_filter) {
        configBean.setUserMemberSearch_filter(search_filter);
    }
    
    public String getUser_attr() {
        return configBean.getUser_attr();
    }

    public void setUser_attr(String user_attr) {
        configBean.setUser_attr(user_attr);
    }
    
    public String getGroupSearch_filter() {
        return configBean.getGroupSearch_filter();
    }

    public void setGroupSearch_filter(String search_filter) {
        configBean.setGroupSearch_filter(search_filter);
    }
    
    public String getGroupMemberSearch_filter() {
        return configBean.getGroupMemberSearch_filter();
    }

    public void setGroupMemberSearch_filter(String search_filter) {
        configBean.setGroupMemberSearch_filter(search_filter);
    }
    
    public String getIsNested() {
        return configBean.getIsNested();
    }

    public void setIsNested(String isNested) {
        configBean.setIsNested(isNested);
    }
    
    public String getBase_dn() {
        return configBean.getBase_dn();
    }

    public void setBase_dn(String base_dn) {
        configBean.setBase_dn(base_dn);
    }

    public void setSecurity_password(String security_password) {
        configBean.setSecurity_password(security_password);
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
