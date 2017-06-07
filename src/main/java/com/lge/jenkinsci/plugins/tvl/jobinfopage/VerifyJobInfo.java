package com.lge.jenkinsci.plugins.tvl.jobinfopage;

import hudson.model.AbstractProject;
import hudson.model.InvisibleAction;
import hudson.model.Run;
import hudson.util.RunList;

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

    public List<Map<String, String>> getData(){
        String name = project.getName();
        RunList r = project.getBuilds();
        Iterator i = r.iterator();
        List<Map<String, String>> list = new ArrayList<>();
        String dateFormat = "yyyy-MM-dd hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        while(i.hasNext()){
            Run each_run = (Run) i.next();
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
            each_r.put("start", simpleDateFormat.format(startDate.getTime()));
            long duration = each_run.getDuration();
            each_r.put("duraiton", String.valueOf(duration));
            long estimataedDuration = each_run.getEstimatedDuration();
            list.add(each_r);
        }
        return list;
    }
}
