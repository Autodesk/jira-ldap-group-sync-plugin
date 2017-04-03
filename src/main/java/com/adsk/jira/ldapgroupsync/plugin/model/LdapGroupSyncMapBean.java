package com.adsk.jira.ldapgroupsync.plugin.model;

public class LdapGroupSyncMapBean {
    private String ldapGroup;
    private String jiraGroup;
    private boolean support;

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
}
