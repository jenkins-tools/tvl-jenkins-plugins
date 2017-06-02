package org.jenkinsci.plugins.jobinfoscreen;

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
