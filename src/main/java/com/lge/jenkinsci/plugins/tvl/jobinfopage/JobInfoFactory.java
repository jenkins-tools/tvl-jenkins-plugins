package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.Extension;
import hudson.model.*;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by sunjoo on 07/06/2017.
 */
@Extension
public class JobInfoFactory extends TransientProjectActionFactory {
    /*
    @Override
    public Descriptor<JobInfoFactory> getDescriptor() {
        return null;
    }
    */

    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        String jobName = target.getName();
        String[] jobNameSplitted = jobName.split("-");
        if (jobNameSplitted[2].equals("verify")){
            return Collections.singletonList(new VerifyJobInfo(target));
        }else {
            return Collections.emptyList();
        }
    }
}
