package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.model.AbstractProject;
import hudson.model.InvisibleAction;

/**
 * Created by sunjoo on 06/06/2017.
 */
public class VerifyJobInfo extends InvisibleAction {

    final private AbstractProject project;

    public VerifyJobInfo(AbstractProject project){
        this.project = project;
    }
}
