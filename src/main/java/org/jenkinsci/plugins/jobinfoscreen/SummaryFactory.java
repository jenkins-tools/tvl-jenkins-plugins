package org.jenkinsci.plugins.jobinfoscreen;

import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.util.RunList;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

@Extension
public class SummaryFactory extends TransientProjectActionFactory implements Describable<SummaryFactory> {

    /**
     * For matrix projects parameter actions are attached to the MatrixProject
     */
    @Override
    public Collection<? extends Action> createFor(
            @SuppressWarnings("rawtypes") AbstractProject target
    ) {

            /*
            try {
                Class<?> mc = Class.forName("hudson.matrix.MatrixConfiguration");
                if (mc.isInstance(target)) {
                    target = (AbstractProject) target.getParent();
                }
            } catch (ClassNotFoundException e) {
                // matrix-project not installe
            }

            if (!isParameterized(target)) {
                return Collections.emptyList();
            }
            */
        String name = target.getName();
        /*
        RunList<Run> r = target.getBuilds();
        Iterator i = r.iterator();
        while(i.hasNext()){
            Run each_run = (Run) i.next();
            System.out.println(name + ": " + each_run.getResult().toString());
        }
        */
        if (name.equals("starfish-gld4tv-official-m16p")){
            return Collections.singletonList(new Summary(target));
        }else{
            return Collections.singletonList(new Summary(target));
//            return Collections.emptyList();
        }
    }


/*
    private boolean isParameterized(final AbstractProject<?, ?> project) {
        return definitionProperty(project) != null;
    }
    */


    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public Descriptor<SummaryFactory> getDescriptor() {
        return DESCRIPTOR;
    }

    public static class DescriptorImpl extends Descriptor<SummaryFactory> {
        private String branches;
        private String buildRepoUrl;
        private String username;
        private String password;

        public DescriptorImpl(){
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            branches = formData.getString("branches");
            buildRepoUrl = formData.getString("buildRepoUrl");
            username = formData.getString("username");
            password = formData.getString("password");
            save();
            return super.configure(req, formData);
        }

        @Override
        public String getDisplayName() {
            return "Branch name";
        }

        @Override
        public String getHelpFile() {
            return "/help/parameter/string.html";
        }

        public String getBranches() {
            return branches;
        }

        public String getBuildRepoUrl() {
            return buildRepoUrl;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}