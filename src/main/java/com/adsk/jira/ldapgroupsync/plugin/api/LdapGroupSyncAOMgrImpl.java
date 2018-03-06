/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.api;

import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import java.util.ArrayList;
import java.util.List;
import net.java.ao.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncAOMgrImpl implements LdapGroupSyncAOMgr {
    private static final Logger logger = Logger.getLogger(LdapGroupSyncAOMgrImpl.class);    
    
    private final ActiveObjects ao;     
    private LdapGroupSyncAOMgrImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    public ActiveObjects getActiveObjects() {
        return this.ao;
    }        
    
    public List<LdapGroupSyncMapBean> getGroupsMapProperties() {        
        final LdapGroupSyncMap[] maps = getActiveObjects()
                .find(LdapGroupSyncMap.class, Query.select());        
        List<LdapGroupSyncMapBean> configList = new ArrayList<LdapGroupSyncMapBean>();
        for(LdapGroupSyncMap map : maps){
            LdapGroupSyncMapBean bean = new LdapGroupSyncMapBean();
            bean.setConfigId(map.getID());
            bean.setLdapGroup(map.getLdapGroup());
            bean.setJiraGroup(map.getJiraGroup());
            configList.add(bean);
        }
        return configList;
    }
    
    public void addGroupsMapProperty(LdapGroupSyncMapBean configBean) {
        final LdapGroupSyncMap map = ao.create(LdapGroupSyncMap.class);
        map.setLdapGroup(configBean.getLdapGroup());
        map.setJiraGroup(configBean.getJiraGroup());
        map.save();
    }
    
    public void setGroupsMapProperty(LdapGroupSyncMapBean configBean) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, 
                Query.select().where("ID = ?", configBean.getConfigId()));
        if(maps.length > 0) {
            final LdapGroupSyncMap map = maps[0];
            map.setLdapGroup(configBean.getLdapGroup());
            map.setJiraGroup(configBean.getJiraGroup());
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
            LdapGroupSyncMap map = maps[0];            
            ao.delete(map);
        }
    }

    public LdapGroupSyncMapBean getGroupsMapProperty(long configId) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select().where("ID = ?", configId));
        if(maps.length > 0) {
            LdapGroupSyncMap map = maps[0];
            LdapGroupSyncMapBean bean = new LdapGroupSyncMapBean();
            bean.setConfigId(map.getID());
            bean.setLdapGroup(map.getLdapGroup());
            bean.setJiraGroup(map.getJiraGroup());
            return bean;
        }
        return null;
    }        
}
