package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.Extension;
import hudson.model.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

    public static class DescriptorForJobInfoFactory extends Descriptor<JobInfoFactory> {
        private String tvlArchiveRootUrl;
        private String tvlArchiveOfficialPath;
        private String tvlArchiveVerifyPath;
        private String svlArchiveRootUrl;
        private String svlArchiveOfficialPath;
        private String svlArchiveVerifyPath;
        private String tvlJenkinsUrl;
        private String svlJenkinsUrl;
        private String wallGitwebUrl;

        public DescriptorForJobInfoFactory() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            tvlArchiveRootUrl = formData.getString("tvlArchiveRootUrl");
            tvlArchiveOfficialPath = formData.getString("tvlArchiveOfficialPath");
            tvlArchiveVerifyPath = formData.getString("tvlArchiveVerifyPath");
            svlArchiveRootUrl = formData.getString("svlArchiveRootUrl");
            svlArchiveOfficialPath = formData.getString("svlArchiveOfficialPath");
            svlArchiveVerifyPath = formData.getString("svlArchiveVerifyPath");
            tvlJenkinsUrl = formData.getString("tvlJenkinsUrl");
            svlJenkinsUrl = formData.getString("svlJenkinsUrl");
            wallGitwebUrl = formData.getString("wallGitwebUrl");
            save();
            return super.configure(req, formData);
        }

        public String getTvlArchiveRootUrl() {
            return tvlArchiveRootUrl;
        }

        public String getTvlArchiveOfficialPath() {
            return tvlArchiveOfficialPath;
        }

        public String getTvlArchiveVerifyPath() {
            return tvlArchiveVerifyPath;
        }

        public String getSvlArchiveRootUrl() {
            return svlArchiveRootUrl;
        }

        public String getSvlArchiveOfficialPath() {
            return svlArchiveOfficialPath;
        }

        public String getSvlArchiveVerifyPath() {
            return svlArchiveVerifyPath;
        }

        public String getTvlJenkinsUrl() {
            return tvlJenkinsUrl;
        }

        public String getSvlJenkinsUrl() {
            return svlJenkinsUrl;
        }

        public String getWallGitwebUrl() {
            return wallGitwebUrl;
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
        String[] jobTypes = {"verify", "integrate"};
        if (jobNameSplitted.length == 4) {
            String jobType = jobNameSplitted[2];
            if (Arrays.asList(jobTypes).contains(jobType)) {
                return Collections.singletonList(new VerifyJobInfo(target));
            } else if (jobNameSplitted[2].equals("official")) {
                return Collections.singletonList(new OfficialJobInfo(target));
            }
        }
        if (jobNameSplitted.length > 2 && jobNameSplitted[0].equals("clean")) {
            return Collections.singletonList(new VerifyJobInfo(target));
        }
        return Collections.singletonList(new GeneralJobInfo(target));
    }

    public static String convertDuration(long duration) {
        String durationStr = "";
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        if (hours == 0) {
            durationStr = String.format("%d min, %d sec", minutes, seconds - TimeUnit.MINUTES.toSeconds(minutes));
        } else {
            durationStr = String.format("%d hour, %d min, %d sec",
                    hours,
                    minutes - TimeUnit.HOURS.toMinutes(hours),
                    seconds - TimeUnit.MINUTES.toSeconds(minutes));
        }
        return durationStr;
    }
}
