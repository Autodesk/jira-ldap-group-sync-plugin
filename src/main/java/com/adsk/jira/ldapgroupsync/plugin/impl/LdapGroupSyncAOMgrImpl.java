/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.impl;

import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.ArrayList;
import java.util.List;
import net.java.ao.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncAOMgrImpl implements LdapGroupSyncAOMgr {
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncAOMgrImpl.class);
    private static LdapGroupSyncAOMgr ldapGroupSyncAOMgr = null;
    private ActiveObjects ao = null;     
    private LdapGroupSyncAOMgrImpl() {
        this.ao = ComponentAccessor.getOSGiComponentInstanceOfType(LdapGroupSyncAOComp.class)
                .getActiveObjects();
    }
    
    public static LdapGroupSyncAOMgr getInstance() {
        if( ldapGroupSyncAOMgr == null ) {
            ldapGroupSyncAOMgr = new LdapGroupSyncAOMgrImpl();
        }
        return ldapGroupSyncAOMgr;
    }

    public ActiveObjects getActiveObjects() {
        return this.ao;
    }
    
    public List<LdapGroupSyncMapBean> getSupportedGroupsMapProperties() {
        final LdapGroupSyncMap[] maps = getActiveObjects()
                .find(LdapGroupSyncMap.class, Query.select().where("SUPPORT = ?", true));
        if(maps.length > 0) {
            List<LdapGroupSyncMapBean> configList = new ArrayList<LdapGroupSyncMapBean>();
            for(LdapGroupSyncMap map : maps){
                LdapGroupSyncMapBean bean = new LdapGroupSyncMapBean();
                bean.setConfigId(map.getID());
                bean.setLdapGroup(map.getLdapGroup());
                bean.setJiraGroup(map.getJiraGroup());
                bean.setSupport(map.getSupport());
                configList.add(bean);
            }
            return configList;
        }
        return null;
    }
    
    public List<LdapGroupSyncMapBean> getAllGroupsMapProperties() {        
        final LdapGroupSyncMap[] maps = getActiveObjects()
                .find(LdapGroupSyncMap.class, Query.select());
        if(maps.length > 0) {
            List<LdapGroupSyncMapBean> configList = new ArrayList<LdapGroupSyncMapBean>();
            for(LdapGroupSyncMap map : maps){
                LdapGroupSyncMapBean bean = new LdapGroupSyncMapBean();
                bean.setConfigId(map.getID());
                bean.setLdapGroup(map.getLdapGroup());
                bean.setJiraGroup(map.getJiraGroup());
                bean.setSupport(map.getSupport());
                configList.add(bean);
            }
            return configList;
        }
        return null;
    }
    
    public void addGroupsMapProperty(LdapGroupSyncMapBean configBean) {
        final LdapGroupSyncMap map = ao.create(LdapGroupSyncMap.class);
        map.setLdapGroup(configBean.getLdapGroup());
        map.setJiraGroup(configBean.getJiraGroup());
        map.setSupport(configBean.isSupport());
        map.save();
    }
    
    public void setGroupsMapProperty(LdapGroupSyncMapBean configBean) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, 
                Query.select().where("ID = ?", configBean.getConfigId()));
        if(maps.length > 0) {
            final LdapGroupSyncMap map = maps[0];
            map.setLdapGroup(configBean.getLdapGroup());
            map.setJiraGroup(configBean.getJiraGroup());
            map.setSupport(configBean.isSupport());
            map.save();
        }
    }
    
    public boolean findGroupsMapProperty(LdapGroupSyncMapBean configBean) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, 
                Query.select().where("LDAP_GROUP = ? OR JIRA_GROUP = ?", 
                configBean.getLdapGroup(), configBean.getJiraGroup()));
        return maps.length > 0;
    }
    
    public boolean findGroupsMapProperty2(LdapGroupSyncMapBean configBean) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, 
                Query.select().where("ID != ? AND (LDAP_GROUP = ? OR JIRA_GROUP = ?)", 
                configBean.getConfigId(), configBean.getLdapGroup(), configBean.getJiraGroup()));
        return maps.length > 0;
    }
    
    public void removeGroupsMapProperty(long configId) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, 
                Query.select().where("ID = ?", configId));
        if(maps.length > 0) {
            ao.delete(maps[0]);
        }
    }
    
    public boolean isJiraGroupNotInSupport(String jiraGroup) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select()
                .where("JIRA_GROUP = ? AND SUPPORT = ?", jiraGroup, false));
        return maps.length > 0;
    }

    public LdapGroupSyncMapBean getGroupsMapProperty(long configId) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select().where("ID = ?", configId));
        if(maps.length > 0) {
            LdapGroupSyncMap map = maps[0];
            LdapGroupSyncMapBean bean = new LdapGroupSyncMapBean();
            bean.setConfigId(map.getID());
            bean.setLdapGroup(map.getLdapGroup());
            bean.setJiraGroup(map.getJiraGroup());
            bean.setSupport(map.getSupport());
            return bean;
        }
        return null;
    }
}
