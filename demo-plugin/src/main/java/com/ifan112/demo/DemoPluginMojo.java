package com.ifan112.demo;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @goal build-info
 * @phase pre-integration-test
 */
public class DemoPluginMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${build-info.prefix}"
     * default-value="+++"
     */
    private String prefix;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Build build = project.getBuild();

        getLog().info("\n======================= Project build info: =======================\n");

        getLog().info(prefix + "\t" + build.getOutputDirectory());
        getLog().info(prefix + "\t" + build.getSourceDirectory());
        getLog().info(prefix + "\t" + build.getTestOutputDirectory());
        getLog().info(prefix + "\t" + build.getTestSourceDirectory());

        getLog().info("\n======================= Project build info: =======================\n");
    }
}
