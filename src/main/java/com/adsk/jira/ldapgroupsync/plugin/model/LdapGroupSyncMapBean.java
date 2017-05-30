package com.adsk.jira.ldapgroupsync.plugin.model;

public class LdapGroupSyncMapBean {
    private long configId;
    private String ldapGroup;
    private String jiraGroup;
    private boolean support;

    public long getConfigId() {
        return configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }        
    
    public String getLdapGroup() {
        return ldapGroup;
    }

    public void setLdapGroup(String ldapGroup) {
        this.ldapGroup = ldapGroup;
    }

    public String getJiraGroup() {
        return jiraGroup;
    }

    public void setJiraGroup(String jiraGroup) {
        this.jiraGroup = jiraGroup;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }
    
    public void clear() {
        this.ldapGroup = null;
        this.jiraGroup=null;
    }
}
