package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.Extension;
import hudson.model.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by sunjoo on 07/06/2017.
 */
@Extension
public class JobInfoFactory extends TransientProjectActionFactory implements Describable<JobInfoFactory> {
    @Override
    public Descriptor<JobInfoFactory> getDescriptor() {
        return null;
    }

    @Extension
    public static final DescriptorForJobInfoFactory DESCRIPTOR = new DescriptorForJobInfoFactory();

    public static class DescriptorForJobInfoFactory extends Descriptor<JobInfoFactory>{
        private String tvlArchiveRootUrl;
        private String svlArchiveRootUrl;
        private String tvlJenkinsUrl;
        private String svlJenkinsUrl;

        public DescriptorForJobInfoFactory(){
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException{
            tvlArchiveRootUrl = formData.getString("tvlArchiveRootUrl");
            svlArchiveRootUrl = formData.getString("svlArchiveRootUrl");
            tvlJenkinsUrl = formData.getString("tvlJenkinsUrl");
            svlJenkinsUrl = formData.getString("svlJenkinsUrl");
            save();
            return super.configure(req, formData);
        }

        public String getTvlArchiveRootUrl(){
            return tvlArchiveRootUrl;
        }

        public String getSvlArchiveRootUrl(){
            return svlArchiveRootUrl;
        }

        public String getTvlJenkinsUrl(){
            return tvlJenkinsUrl;
        }

        public String getSvlJenkinsUrl(){
            return svlJenkinsUrl;
        }

        @Override
        public String getDisplayName() {
            return "Job Information Display";
        }
    }

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
