package com.lge.jenkinsci.plugins.tvl;

import hudson.Extension;
import hudson.model.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sunjoo on 27/04/2017.
 */
public class BuildBranchParameter extends SimpleParameterDefinition{

    private String branchName;

    @DataBoundConstructor
    public BuildBranchParameter(String name, String description){
        super(name, description);
        this.branchName = "";
    }

    @Exported
    public List<String> getBranches() throws IOException, InterruptedException{
        String[] temp = getDescriptor().getBranches().split("\n");
        List<String> choices = Arrays.asList(temp);
        return choices;
    }

    public String getBranchName(){
        return branchName;
    }

    @DataBoundSetter
    public void setBranchName(String branchName){
        this.branchName = branchName;
    }

    @Override
    public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof StringParameterValue) {
            StringParameterValue value = (StringParameterValue) defaultValue;
            return new BuildBranchParameter(getName(), getDescription());
        } else {
            return this;
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        private String branches;

        public DescriptorImpl(){load();}

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            branches = formData.getString("branches");
            save();
            return super.configure(req,formData);
        }

        @Override
        public String getDisplayName() {
            return "Branch name";
        }

        @Override
        public String getHelpFile() {
            return "/help/parameter/string.html";
        }

        public String getBranches(){
            return branches;
        }
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        //For request
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo.accumulate("description", ""));
        value.setDescription("");
        return value;
    }

    @Override
    public ParameterValue createValue(String value) {
        return new StringParameterValue(getName(), value, "");
    }
}
