package org.jpragey.gradle.commonmark

import org.commonmark.Extension
import org.commonmark.node.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

import org.gradle.api.tasks.TaskAction

import groovy.lang.Closure

class CommonMarkTask extends DefaultTask {

	public static String defaultSourceDir = "doc"
	public static String defaultOutputDir = "gen-doc"
	
	
//	@Input
	Closure<String> htmlBuilder = null
	
//	@InputFile
	File sourceDir = new File(project.projectDir, defaultSourceDir)
//	@InputFile
	File outputDir = new File(project.buildDir, defaultOutputDir)
	
//	@Input
	List<Extension> cmarkExtensions = new ArrayList<Extension>(); 
	
	public CommonMarkTask() {
	}
	
	
	@TaskAction
	def action() {
//		logger.info("== compiling doc: ${sourceDir} to dir:${outputDir}")
		new CommonMarkWorker(htmlBuilder, sourceDir, outputDir, cmarkExtensions)
			.action()
	}
}
