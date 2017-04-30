package com.lge.jenkinsci.plugins.tvl;

import hudson.Extension;
import hudson.model.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by sunjoo on 27/04/2017.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import static java.util.stream.Collectors.toList;

public class BuildBranchParameter extends SimpleParameterDefinition {

    private static final Logger LOGGER = Logger.getLogger( BuildBranchParameter.class.getName() );
    private String branchName;

    @DataBoundConstructor
    public BuildBranchParameter(String name, String description) {
        super(name, description);
        this.branchName = "";
    }

    /**
     * Generate a list of branches used for 'build_codename'
     * This method run <code>buildRepoUrl</code> API and get branche names.
     * Also it get branch names with <code>getBranches()</code> and merge them
     * with a list of branches extracted from <code>buildRepoUrl</code>'s result
     * When getting branch names from a build repository, some rules are applied and
     * only designated branches will be remained.
     * @return List of branches
     * @throws IOException
     * @throws InterruptedException
     */
    @Exported
    public List<String> getBranches() throws IOException, InterruptedException {
        String[] temp = getDescriptor().getBranches().trim().split("\n");
        String username = getDescriptor().getUsername();
        String password = getDescriptor().getPassword();
        String buildRepoUrl = getDescriptor().getBuildRepoUrl();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password)
        );
        List<String> choices = new ArrayList<>();
        if (!buildRepoUrl.equals("") && !username.equals("") && !password.equals("")) {
            LOGGER.info("Get a list of branches from " + buildRepoUrl);
            HttpGet get_req = new HttpGet(buildRepoUrl);
            HttpResponse response;
            response = httpClient.execute(get_req);
            HttpEntity entity = response.getEntity();
            LOGGER.info("----------------------------------------");
            LOGGER.info(response.getStatusLine().toString());
            String responseJson = "";
            if (entity != null) {
               LOGGER.info("Response content length: " + entity.getContentLength());
            }
            if (entity != null) {
                responseJson = EntityUtils.toString(entity).replace(")]}'\n", "");
            }
            httpClient.getConnectionManager().shutdown();
            if (response.getStatusLine().getStatusCode() == 200) {
                JSONArray branches = (JSONArray) JSONSerializer.toJSON(responseJson);
                List<String> rd = Arrays.asList(responseJson.split("\n"));
                Arrays.asList(branches.toArray()).stream()
                        .map(d -> (JSONObject) d)
                        .map(f -> (String) f.get("ref"))
                        .map(g -> g.replace("refs/heads/", ""))
                        .map(h -> h.replace("refs/meta/", ""))
                        .filter(i -> !i.equals("HEAD") && !i.equals("config"))
                        .filter(j -> j.charAt(0) == '@')
                        .filter(k -> k.split("\\.").length == 1)
                        .forEach(l -> choices.add(l));
                choices.add("sunjoo");
                choices.add("soyul");
            }
            else{
                LOGGER.warning("Can't get a list of branches from " + buildRepoUrl);
                LOGGER.warning(Integer.toString(response.getStatusLine().getStatusCode()));
                LOGGER.warning(response.getStatusLine().getReasonPhrase());
            }
        }else{
            LOGGER.info("Don't get a list of branches from a build repository");
            LOGGER.info(buildRepoUrl);
            LOGGER.info(username);
            if (password.equals("")){
                LOGGER.info("Password is not defined");
            }
        }

        LOGGER.info("Add branches defined in global configuration");
        for (String i : temp) {
            choices.add(i);
        }
        return choices;
    }

    public String getBranchName() {
        return branchName;
    }

    @DataBoundSetter
    public void setBranchName(String branchName) {
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
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        private String branches;
        private String buildRepoUrl;
        private String username;
        private String password;

        public DescriptorImpl() {
            load();
        }

        /**
         * Get information from a global configuration page
         * This configuration provide list of branches(one branch per a line),
         * Build repository's url, Gerri username and password
         * @param req
         * @param formData Form data with parameters: branches, buildRepoUrl, username, password
         * @return True or False when calling parent's <code>configure()</code> method
         * @throws FormException
         */
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
