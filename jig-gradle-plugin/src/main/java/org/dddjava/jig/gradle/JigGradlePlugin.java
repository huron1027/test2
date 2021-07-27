package org.dddjava.jig.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

public class JigGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ExtensionContainer extensions = project.getExtensions();
        extensions.create("jig", JigConfig.class);
        TaskContainer tasks = project.getTasks();

        JigReportsTask jigReports = tasks.create("jigReports", JigReportsTask.class);
        jigReports.setGroup("JIG");
        jigReports.setDescription("Generates JIG documentation for the main source code.");

        VerifyJigEnvironmentTask verifyTask = tasks.create("verifyJigEnvironment", VerifyJigEnvironmentTask.class);
        verifyTask.setGroup("JIG");
        verifyTask.setDescription("Verify JIG environment.");
    }
}
