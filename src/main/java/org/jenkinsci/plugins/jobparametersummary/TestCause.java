package org.jenkinsci.plugins.jobparametersummary;

import hudson.Extension;
import hudson.model.Cause;

/**
 * Created by sunjoo on 15/05/2017.
 */
public class TestCause extends Cause {

    @Override
    public String getShortDescription() {
        return "Cause";
    }
}
