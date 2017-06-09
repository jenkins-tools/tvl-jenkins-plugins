package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.model.*;
import hudson.util.RunList;
import jenkins.model.Jenkins;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sunjoo on 06/06/2017.
 */
public class VerifyJobInfo extends InvisibleAction {

    final private AbstractProject project;

    public VerifyJobInfo(AbstractProject project) {
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

    public List<Map<String, String>> getData() {
        String svlArchiveRootUrl = JobInfoFactory.DESCRIPTOR.getSvlArchiveRootUrl();
        String svlArchiveVerifyPath = JobInfoFactory.DESCRIPTOR.getSvlArchiveVerifyPath();
        String tvlArchiveRootUrl = JobInfoFactory.DESCRIPTOR.getTvlArchiveRootUrl();
        String tvlArchiveVerifyPath = JobInfoFactory.DESCRIPTOR.getTvlArchiveVerifyPath();
        String svlJenkinsUrl = JobInfoFactory.DESCRIPTOR.getSvlJenkinsUrl();
        String tvlJenkinsUrl = JobInfoFactory.DESCRIPTOR.getTvlJenkinsUrl();
        String archiveRootUrl = "";
        Jenkins instance = Jenkins.getInstance();
        String rootUrl = instance.getRootUrl();
        if (rootUrl.equals(tvlJenkinsUrl)) {
            if (tvlArchiveVerifyPath == null || tvlArchiveVerifyPath.equals("")) {
                archiveRootUrl = tvlArchiveRootUrl;
            } else {
                archiveRootUrl = tvlArchiveRootUrl + handlePathValue(tvlArchiveVerifyPath);
            }
        } else if (rootUrl.equals(svlJenkinsUrl)) {
            if (svlArchiveVerifyPath == null || svlArchiveVerifyPath.equals("")) {
                archiveRootUrl = svlArchiveRootUrl;
            } else {
                archiveRootUrl = svlArchiveRootUrl + handlePathValue(svlArchiveVerifyPath);
            }
        }
        String name = project.getName();
        RunList r = project.getBuilds();
        Iterator i = r.iterator();
        List<Map<String, String>> list = new ArrayList<>();
        String dateFormat = "yyyy-MM-dd hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        LocalDateTime limitDay = LocalDateTime.now().minusDays(14);
        while (i.hasNext()) {
            Run each_run = (Run) i.next();
            List<Action> actions = (List<Action>) each_run.getAllActions();
            Map<String, String> each_r = new HashMap<String, String>();
            String result = "";
            String description = "";
            long duration = 0L;
            each_r.put("name", name);
            int number = each_run.getNumber();
            each_r.put("number", String.valueOf(number));
            long startTime = each_run.getStartTimeInMillis();
            if (startTime < limitDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                continue;
            }
            Date startDate = new Date(startTime);
            each_r.put("when", simpleDateFormat.format(startDate.getTime()));
            try {
                result = each_run.getResult().toString();
            } catch (Exception e) {
                result = "In-Progress";
            }

            each_r.put("result", result);
            String gerritProject = "";
            String gerritChangeUrl = "";
            String gerritChangeOwnerName = "";
            String gerritChangeNumber = "";
            String gerritPatchsetNumber = "";
            String downloadUrl = "";
            try {
                description = each_run.getDescription();
                duration = each_run.getDuration();

                List<Object> parametersActions  = actions.stream().filter(action -> action instanceof ParametersAction).collect(Collectors.toList());
                for (Object parametersAction : parametersActions) {
                    List<ParameterValue> parameterValues = ((ParametersAction) parametersAction).getAllParameters();
                    for (ParameterValue eachParameter : parameterValues){
                        String parameterName = eachParameter.getName();
                        String parameterValue = eachParameter.getValue().toString();
                        if (parameterName.equals("GERRIT_PROJECT")){
                            gerritProject = parameterValue;
                        }
                        else if (parameterName.equals("GERRIT_CHANGE_URL")){
                            gerritChangeUrl = parameterValue;
                        }
                        else if (parameterName.equals("GERRIT_CHANGE_OWNER_NAME")){
                            gerritChangeOwnerName = parameterValue;
                        }
                        else if (parameterName.equals("GERRIT_CHANGE_NUMBER")){
                            gerritChangeNumber = parameterValue;
                        }
                        else if (parameterName.equals("GERRIT_PATCHSET_NUMBER")){
                            gerritPatchsetNumber = parameterValue;
                        }
                    }

                }
                downloadUrl = archiveRootUrl + name + "/" + String.valueOf(number);
                each_r.put("gerrit_project", gerritProject);
                each_r.put("gerrit_change_url", gerritChangeUrl);
                each_r.put("gerrit_change_owner_name", gerritChangeOwnerName);
                each_r.put("gerrit_change_number", gerritChangeNumber);
                each_r.put("gerrit_patchset_number", gerritPatchsetNumber);
                each_r.put("download_url", downloadUrl);
            } catch (Exception e) {
                description = "";
                duration = each_run.getEstimatedDuration();
            }
            each_r.put("description", description);
            each_r.put("duration", JobInfoFactory.convertDuration(duration));
            each_r.put("url", rootUrl + each_run.getUrl());
            list.add(each_r);
        }
        return list;
    }
}
