package org.jpragey.cmarkplugin

import spock.lang.Specification

class TestCommonMark extends Specification /*  GroovyTestCase */{


	File projectDir = new File(System.getProperty("user.dir") + "/testProjects/simpleProject");
	URL projectClassPathResources = getClass().classLoader.findResource("plugin-classpath.txt");
	def pluginClasspath = projectClassPathResources.readLines().collect{new File(it)}

	def 'check ancestor directory building'() {
		when:
		//def level = 3
		String s = Collections.nCopies(level, "../").join('')
		
		then:
		s == path
		
		where:
		level 	| path
		0		| ''
		1		| '../'
		3		| '../../../'
		
	}

	
}



