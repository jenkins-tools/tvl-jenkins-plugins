package org.jenkinsci.plugins.jobinfoscreen;

import hudson.model.*;
import hudson.util.RunList;
import hudson.util.Secret;

import java.text.SimpleDateFormat;
import java.util.*;

public class Summary extends InvisibleAction {

    private static final String PASSWORD_MASK = "********";
    private static final String QUOTE_STRING = "\"";

    final private AbstractProject<?, ?> project;

    public Summary(@SuppressWarnings("rawtypes") AbstractProject project) {

        this.project = project;
    }

    public List<ParameterDefinition> getParameters() {
        return definitionProperty(project).getParameterDefinitions();
    }

    @Override
    public String toString() {

        return "Job parameter summary for " + project.toString();
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

    private static ParametersDefinitionProperty definitionProperty(
            final AbstractProject<?, ?> project
    ) {

        return project.getProperty(ParametersDefinitionProperty.class);
    }

    private static String quote(String s) {
        return QUOTE_STRING + s + QUOTE_STRING;
    }

    /**
     * Get default value for {@link ParameterDefinition} that has any and it can be displayed
     */
    public String getDefault(final ParameterDefinition d) {

        final ParameterValue v = d.getDefaultParameterValue();

        String res = null;
        if (d instanceof BooleanParameterDefinition) {
            res = Boolean.toString(((BooleanParameterValue) v).value);
        } else if (d instanceof StringParameterDefinition) {
            res = quote(((StringParameterValue) v).value);
        } else if (d instanceof PasswordParameterDefinition) {
            // check whether we have a default value and return a printable mask if we do
            String password = Secret.toString(((PasswordParameterValue) v).getValue());
            if (!password.isEmpty()) {
                res = quote(PASSWORD_MASK);
            }
        } else if (d instanceof ChoiceParameterDefinition) {
            res = quote(((StringParameterValue) v).value);
        }

        return res;
    }

}
