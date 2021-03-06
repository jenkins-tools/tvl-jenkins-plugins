package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.model.*;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSetList;
import hudson.scm.ChangeLogSet;
import hudson.util.RunList;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by sunjoo on 06/06/2017.
 */
public class OfficialJobInfo extends InvisibleAction {

    final private AbstractProject project;

    public OfficialJobInfo(AbstractProject project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Job information page for " + project.toString();
    }

    public String getLoggedUser() {
        String logged_user_name = "";
        try {
            User logged_user = User.current();
            logged_user_name = logged_user.getId();
        } catch (Exception e) {
            logged_user_name = "None";
        }
        return logged_user_name;
    }

    private String handlePathValue(String path) {
        if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        return path;
    }

    public List<Map<String, String>> getData() throws IOException {
        String svlArchiveRootUrl = JobInfoFactory.DESCRIPTOR.getSvlArchiveRootUrl();
        String svlArchiveOfficialPath = JobInfoFactory.DESCRIPTOR.getSvlArchiveOfficialPath();
        String tvlArchiveRootUrl = JobInfoFactory.DESCRIPTOR.getTvlArchiveRootUrl();
        String tvlArchiveOfficialPath = JobInfoFactory.DESCRIPTOR.getTvlArchiveOfficialPath();
        String svlJenkinsUrl = JobInfoFactory.DESCRIPTOR.getSvlJenkinsUrl();
        String tvlJenkinsUrl = JobInfoFactory.DESCRIPTOR.getTvlJenkinsUrl();
        String wallGitwebUrl = JobInfoFactory.DESCRIPTOR.getWallGitwebUrl();
        String archiveRootUrl = "";
        String downloadUrl = "";
        Jenkins instance = Jenkins.getInstance();
        String rootUrl = instance.getRootUrl();
        if (rootUrl.equals(tvlJenkinsUrl)) {
            if (tvlArchiveOfficialPath == null || tvlArchiveOfficialPath.equals("")) {
                archiveRootUrl = tvlArchiveRootUrl;
            } else {
                archiveRootUrl = tvlArchiveRootUrl + handlePathValue(tvlArchiveOfficialPath);
            }
        } else if (rootUrl.equals(svlJenkinsUrl)) {
            if (svlArchiveOfficialPath == null || svlArchiveOfficialPath.equals("")) {
                archiveRootUrl = svlArchiveRootUrl;
            } else {
                archiveRootUrl = svlArchiveRootUrl + handlePathValue(svlArchiveOfficialPath);
            }
        }
        String name = project.getName();
        RunList r = project.getBuilds();
        Iterator i = r.iterator();
        List<Map<String, String>> list = new ArrayList<>();
        String dateFormat = "yyyy-MM-dd hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        LocalDateTime limitDay = LocalDateTime.now().minusDays(10);
        while (i.hasNext()) {
            Run each_run = (Run) i.next();
            List<Action> actions = (List<Action>) each_run.getAllActions();
            String result = "";
            Map<String, String> each_r = new HashMap<String, String>();

            each_r.put("name", name);
            int number = each_run.getNumber();
            each_r.put("number", String.valueOf(number));
            String description = each_run.getDescription();
            long startTime = each_run.getStartTimeInMillis();
            Date startDate = new Date(startTime);
            each_r.put("when", simpleDateFormat.format(startDate.getTime()));
            try {
                result = each_run.getResult().toString();
            } catch (Exception e) {
                result = "In-Progress";
                each_r.put("result", result);
                list.add(each_r);
                continue;
            }
            each_r.put("result", result);

            long duration = each_run.getDuration();
            each_r.put("duration", JobInfoFactory.convertDuration(duration));
            long estimataedDuration = each_run.getEstimatedDuration();
            each_r.put("url", rootUrl + each_run.getUrl());

            if (archiveRootUrl != null && !archiveRootUrl.equals("")){
                downloadUrl = archiveRootUrl + name + "/" + String.valueOf(number);
            }
            each_r.put("download_url", downloadUrl);

            if (description != null) {
                Document desc = Jsoup.parse(description);
                Elements li_version = desc.select("li font");
                Elements li_elements = desc.select("li");
                String version = li_version.stream().map(e -> e.text()).collect(Collectors.joining("\n"));
                String buildRepoDescription = li_elements.stream().filter(e -> e.text().startsWith("builds")).map(e -> e.text()).collect(Collectors.joining("\n"));
                each_r.put("version", version);
                each_r.put("buildrepo", buildRepoDescription);
                List<ChangeLogSet> changeSets = ((AbstractBuild) each_run).getChangeSets();

                List<Object> changeSetLists = changeSets.stream()
                        .filter(c -> c instanceof GitChangeSetList).collect(Collectors.toList());

                String gitComment = "";
                String gitWhen = "";
                String gitCommitId = "";
                String gitId = "";
                for (Object gitChangeSetList : changeSetLists) {
                    List<Object> gitChangeSets = Arrays.stream(((GitChangeSetList) gitChangeSetList).getItems())
                            .filter(c -> c instanceof GitChangeSet).collect(Collectors.toList());
                    gitWhen = gitChangeSets.stream().map(c -> ((GitChangeSet) c).getDate())
                            .collect(Collectors.joining("\n"));
                    gitCommitId = gitChangeSets.stream().map(c -> ((GitChangeSet) c).getCommitId())
                            .collect(Collectors.joining("\n"));
                    gitId = gitChangeSets.stream().map(c -> ((GitChangeSet) c).getId())
                            .collect(Collectors.joining("\n"));
                }

                String gitCommitLink = "";
                String gitTagLink = "";
                if (wallGitwebUrl != null) {
                    gitCommitLink = wallGitwebUrl + "a=commit;h=" + gitCommitId;
                    gitTagLink = wallGitwebUrl + "a=tag;h=refs/tags/" + buildRepoDescription;
                }
                each_r.put("gitcommitid", gitCommitId);
                each_r.put("gitcommitid", gitCommitId);
                each_r.put("gitid", gitId);
                each_r.put("gitcommenturl", rootUrl + each_run.getUrl() + "changes#detail0");
                each_r.put("gitwhen", gitWhen);
                each_r.put("gitcommitlink", gitCommitLink);
                each_r.put("gittaglink", gitTagLink);
            }
            list.add(each_r);
        }
        return list;


    }
}
