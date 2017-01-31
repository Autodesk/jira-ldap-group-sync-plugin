/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.ldapgroupsync.plugin.config;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.tse.jira.ldapgroupsync.plugin.model.LdapGroupSyncConfigBean;
import com.tse.jira.ldapgroupsync.plugin.svc.MyLdapUtils;
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
            String SEARCH_FILTER = configBean.getSearch_filter();
            String BASE_DN = configBean.getBase_dn();
            String MEMBER_ATTR = configBean.getMember_attr();
            String USER_ATTR = configBean.getUser_attr();
            
            LOGGER.debug("LDAP URL -> "+ LDAP_URL);
            LOGGER.debug("SECURITY PRINCIPAL -> "+ SECURITY_PRINCIPAL);
            LOGGER.debug("SEARCH FILTER -> "+ SEARCH_FILTER);
            LOGGER.debug("BASE DN -> "+ BASE_DN);
            LOGGER.debug("MEMBER ATTR -> "+ MEMBER_ATTR);
            LOGGER.debug("USER ATTR -> "+ USER_ATTR);
            
            if( LDAP_URL != null && !"".equals(LDAP_URL) && SECURITY_PRINCIPAL != null && !"".equals(SECURITY_PRINCIPAL) 
                    && SECURITY_PASSWORD != null && !"".equals(SECURITY_PASSWORD) && BASE_DN != null && !"".equals(BASE_DN) ) {
                ldapGroupSyncConfigMgr.setGroupsConfigProperties(configBean);
                
                MyLdapUtils.LDAP_URL = configBean.getLdap_url();
                MyLdapUtils.SECURITY_PRINCIPAL = configBean.getSecurity_principal();
                MyLdapUtils.SECURITY_PASSWORD = configBean.getSecurity_password();
                MyLdapUtils.BASE_DN = configBean.getBase_dn();
                
                if( SEARCH_FILTER != null && !"".equals(SEARCH_FILTER) ) {
                    MyLdapUtils.SEARCH_FILTER = configBean.getSearch_filter();
                } else {
                    MyLdapUtils.SEARCH_FILTER = "(&(objectClass=group)(sAMAccountName={0}))"; //default
                }
                if( MEMBER_ATTR != null && !"".equals(MEMBER_ATTR) ) { 
                    MyLdapUtils.MEMBER_ATTR = configBean.getMember_attr();
                } else {
                    MyLdapUtils.MEMBER_ATTR = "member"; //default
                }
                if( USER_ATTR != null && !"".equals(USER_ATTR) ) {
                    MyLdapUtils.USER_ATTR = configBean.getUser_attr();
                } else {
                    MyLdapUtils.USER_ATTR = "sAMAccountName"; //default
                }
                MyLdapUtils.destroyLdapContext(); //to pick latest config
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
    
    public String getSearch_filter() {
        return configBean.getSearch_filter();
    }

    public void setSearch_filter(String search_filter) {
        configBean.setSearch_filter(search_filter);
    }
    
    public String getUser_attr() {
        return configBean.getUser_attr();
    }

    public void setUser_attr(String user_attr) {
        configBean.setUser_attr(user_attr);
    }
    
    public String getMember_attr() {
        return configBean.getMember_attr();
    }

    public void setMember_attr(String member_attr) {
        configBean.setMember_attr(member_attr);
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
