/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.api;

import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncDAO;
import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncAOMgr;
import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncAOMgrImpl;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncBean;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
@Path("/run")
public class LdapGroupResource {
    
    private static final Logger LOGGER = Logger.getLogger(LdapGroupResource.class);
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private GlobalPermissionManager permissionManager = null;
    private static Set<String> defaultJiraGroups = null;
    private final LdapGroupSyncAOMgr ldapGroupSyncMgr;
    
    public LdapGroupResource(JiraAuthenticationContext jiraAuthenticationContext, 
            GlobalPermissionManager permissionManager) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.permissionManager = permissionManager;
        this.ldapGroupSyncMgr = LdapGroupSyncAOMgrImpl.getInstance();
        defaultJiraGroups = new HashSet<String>();
        defaultJiraGroups.add("jira-users");
        defaultJiraGroups.add("jira-administrators");
        defaultJiraGroups.add("jira-software-users");
        defaultJiraGroups.add("jira-developers");
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/configs")
    public Response getLdapGroupSyncConfigs() {
        long startTime = System.currentTimeMillis();
        
        ApplicationUser loggedInAppUser = jiraAuthenticationContext.getLoggedInUser();
        
        LOGGER.debug("["+loggedInAppUser.getUsername() +"] Get Configs Started.");
        
        //check permissions
        if( permissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInAppUser) == false ) {
            String authError = ("[Error] Permission denied. System admin access is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(authError).build();
        }
                
        List<LdapGroupSyncMapBean> results = LdapGroupSyncAOMgrImpl.getInstance()
                .getAllGroupsMapProperties();
        
        long totalTime = System.currentTimeMillis() - startTime;        
        LOGGER.debug("["+loggedInAppUser.getUsername()+"] Get Config. Took "+ totalTime/ 1000d +" Seconds");
        return Response.ok(results).build();
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/admin/sync")
    public Response adminLdapGroupSync(LdapGroupSyncBean syncBean) {
            
        long startTime = System.currentTimeMillis();
        
        //message object
        MessageBean messageBean = new MessageBean();
                
        ApplicationUser loggedInAppUser = jiraAuthenticationContext.getLoggedInUser();
        
        LOGGER.debug("["+loggedInAppUser.getUsername() +"] ["+ syncBean.getLdapGroup() +":"+ syncBean.getJiraGroup() +"] Started.");
        
        //check permissions
        if( permissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInAppUser) == false ) {
            messageBean.setMessage("[Error] Permission denied. System admin access is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        //check required paramaters
        if( syncBean.getLdapGroup() == null || "".equals(syncBean.getLdapGroup()) 
                || syncBean.getJiraGroup() == null || "".equals(syncBean.getJiraGroup()) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        if(defaultJiraGroups.contains(syncBean.getJiraGroup().toLowerCase()) ) { //skip not supported
            messageBean.setMessage("This plugin does not support JIRA default group ("+syncBean.getJiraGroup()+"). Skipping!");
            return Response.ok(messageBean).build();
        }
        
        if(ldapGroupSyncMgr.isJiraGroupNotInSupport(syncBean.getJiraGroup()) == true) { //skip not supported
            messageBean.setMessage("This JIRA group ("+syncBean.getJiraGroup()+") is mapped not to support. Skipping!");
            return Response.ok(messageBean).build();
        }
                
        MessageBean result = LdapGroupSyncDAO.getInstance()
                .sync(syncBean.getLdapGroup(), syncBean.getJiraGroup());
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        LOGGER.debug("["+loggedInAppUser.getUsername()+"] ["+ syncBean.getLdapGroup() +":"+ 
                syncBean.getJiraGroup() +"] "+ result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
        
        result.setMessage(result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
        return Response.ok(result).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/self/sync/{ldapJiraGroup}")
    public Response selfLdapGroupSync(@PathParam("ldapJiraGroup") String ldapJiraGroup) {
            
        long startTime = System.currentTimeMillis();
        
        //message object
        MessageBean messageBean = new MessageBean();
                
        ApplicationUser loggedInAppUser = jiraAuthenticationContext.getLoggedInUser();
        
        LOGGER.debug("Self Sync ["+loggedInAppUser.getUsername() +"] ["+ ldapJiraGroup +"] Started.");
        
        //check required paramaters
        if( ldapJiraGroup == null || "".equals(ldapJiraGroup) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        if(defaultJiraGroups.contains(ldapJiraGroup.toLowerCase()) ) { //skip not supported
            messageBean.setMessage("This plugin does not support JIRA default group ("+ldapJiraGroup+"). Skipping!");
            return Response.ok(messageBean).build();
        }
        
        if(ldapGroupSyncMgr.isJiraGroupNotInSupport(ldapJiraGroup) == true) { //skip not supported
            messageBean.setMessage("This JIRA group ("+ldapJiraGroup+") is mapped not to support. Skipping!");
            return Response.ok(messageBean).build();
        }
                
        MessageBean result = LdapGroupSyncDAO.getInstance()
                .sync(ldapJiraGroup, ldapJiraGroup);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        LOGGER.debug("Self Sync ["+loggedInAppUser.getUsername()+"] ["+ 
                ldapJiraGroup +"] "+ result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
        
        result.setMessage(result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
        return Response.ok(result).build();
    }
}
