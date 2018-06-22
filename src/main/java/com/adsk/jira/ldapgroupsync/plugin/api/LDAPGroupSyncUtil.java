/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.api;

import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

/**
 *
 * @author prasadve
 */
public interface LDAPGroupSyncUtil {
    
    public List<String> getUsersInLdapGroup(LdapContext ctx, String groupName);
    
    public List<String> getNestedLdapGroups(LdapContext ctx, String groupName);
    
    public Set<String> getLdapGroupMembers(LdapContext ctx, SearchResult sr, Set<String> users);
    
    public SearchControls getGroupSearchControls();
    
    public SearchControls getMemberSearchControls();
    
    public Properties getLdapProperties();
    
    public LdapContext getLdapContext();
    
    public void destroyLdapContext();
    
    public void createJiraGroup(String groupName);
    
    public List<String> getJiraGroupMembers(String groupName);
    
    public void addUserToJiraGroup(String userName, String groupName);
    
    public void removeUserFromJiraGroup(String userName, String groupName);
    
    public SearchResult getGroupSearchResult(LdapContext ctx, String groupName);
    public long process(LdapContext ctx, String ldap_group, String jira_group);
    public MessageBean sync(LdapContext ctx, String ldap_group, String jira_group);        
    
    public void setLdapUrl(String ldapUrl);
    public void setSecurityPrincipal(String securityPrincipal);
    public void setSecurityPassword(String securityPassword);
    public void setBaseDn(String baseDn);
    public void setGroupSearchFilter(String GroupSearchFilter);
    public void setGroupMemberSearchFilter(String GroupMemberSearchFilter);
    public void setUserMemberSearchFilter(String UserMemberSearchFilter);
    public void setUserAttr(String UserAttr);
    public void setIsNested(String IsNested);
}
