package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.model.*;
import hudson.util.RunList;

import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sunjoo on 06/06/2017.
 */
public class VerifyJobInfo extends InvisibleAction {

    final private AbstractProject project;

    public VerifyJobInfo(AbstractProject project){
        this.project = project;
    }

    @Override
    public String toString() {
        return "Job information page for " + project.toString();
    }

    public String getLoggedUser(){
        String logged_user_name = "";
        try {
            User logged_user = User.current();
            logged_user_name = logged_user.getId();
        }catch(Exception e)
        {
            logged_user_name = "None";
        }
        return logged_user_name;
    }

    public List<Map<String, String>> getData(){
        String name = project.getName();
        RunList r = project.getBuilds();
        Iterator i = r.iterator();
        List<Map<String, String>> list = new ArrayList<>();
        String dateFormat = "yyyy-MM-dd hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        while(i.hasNext()){
            Run each_run = (Run) i.next();
            List<Action> actions = (List<Action>) each_run.getAllActions();
            ParametersAction parameterAction = (ParametersAction) actions.get(0);
            Map<String,String> each_r = new HashMap<String, String>();
            String result = each_run.getResult().toString();
            each_r.put("name", name);
            each_r.put("result", result);
            int number = each_run.getNumber();
            each_r.put("number", String.valueOf(number));
            String description = each_run.getDescription();
            each_r.put("description", description);
            long startTime = each_run.getStartTimeInMillis();
            Date startDate = new Date(startTime);
            each_r.put("when", simpleDateFormat.format(startDate.getTime()));
            long duration = each_run.getDuration();
            each_r.put("duration", String.valueOf(duration));
            long estimataedDuration = each_run.getEstimatedDuration();

            each_r.put("gerrit_project", parameterAction.getParameter("GERRIT_PROJECT").getValue().toString());
            each_r.put("gerrit_change_url", parameterAction.getParameter("GERRIT_CHANGE_URL").getValue().toString());
            each_r.put("gerrit_change_owner_name", parameterAction.getParameter("GERRIT_CHANGE_OWNER_NAME").getValue().toString());
            each_r.put("gerrit_change_number", parameterAction.getParameter("GERRIT_CHANGE_NUMBER").getValue().toString());
            each_r.put("gerrit_patchset_number", parameterAction.getParameter("GERRIT_PATCHSET_NUMBER").getValue().toString());
            list.add(each_r);
        }
        return list;
    }
}
