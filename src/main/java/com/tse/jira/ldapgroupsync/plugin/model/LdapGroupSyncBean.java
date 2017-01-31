/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.ldapgroupsync.plugin.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author prasadve
 */
@JsonAutoDetect
public class LdapGroupSyncBean {
    @JsonProperty
    private String ldapGroup;
    @JsonProperty
    private String jiraGroup;

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
}
