package org.jpragey.cmarkplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonMarkPlugin implements Plugin<Project> {
    void apply(Project project) {

		project.task("commonmark", type: CommonMarkTask) {
			group = "Documentation"
			description = "Compile Markdown to HTML"
			
		}
    }
}
