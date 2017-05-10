package com.lge.jenkinsci.plugins.tvl.view;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.Messages;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


/**
 * Created by sunjoo on 02/05/2017.
 */
public class TestColumn extends ListViewColumn {
    @DataBoundConstructor
    public TestColumn() {
    }

    @Exported
    public String getSampleData(Job job, Jenkins app, AbstractBuild lBuild ) throws IOException {
        Run run = job.getLastSuccessfulBuild();
        AbstractBuild ab = (AbstractBuild) job.getLastSuccessfulBuild();
        BufferedReader br = new BufferedReader(new InputStreamReader(run.getLogInputStream()));
        return ab.getBuiltOnStr();
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {
        @Override
        public String getDisplayName() {
            return "Test Column";
        }

        /**
         * Set this method's return value as 'false' if you want to expose a columen
         * only when you add it.
         * @return false
         */
        @Override
        public boolean shownByDefault() {
            return false;
        }



    }
}
