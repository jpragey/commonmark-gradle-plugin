package org.jpragey.cmarkplugin
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestTemplating extends GroovyTestCase {
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

	File projectDir = new File(System.getProperty("user.dir") + "/testProjects/templateProject")
	File buildDir = new File(projectDir, "build")

    void setUp() {
        if(buildDir.exists()) buildDir.deleteDir()
        buildDir.delete()
    }

    void tearDown() {
        setUp()
    }
	
	void testTemplate() {
		File outputDocDir = new File(buildDir, "gen-doc")
		File htmlIndexFile = new File(outputDocDir, "index.html")
		
		def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("commonmark")
                .build()

        assertEquals(SUCCESS, result.task(":commonmark").getOutcome())
		assertTrue(outputDocDir.exists())
		
    	assertTrue(htmlIndexFile.exists())
		assertTrue(htmlIndexFile.getText().contains('<html>'))
		assertTrue(htmlIndexFile.getText().contains('<p>Some text</p>'))
		assertTrue(htmlIndexFile.getText().contains('''"resources/main.css'''))
    }
}
