package org.jpragey.gradle.commonmark
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestRealCommonmarkBuild extends GroovyTestCase 
{
    def projectDir = new File(System.getProperty("user.dir") + "/testProjects/cmarkProject")
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

	def buildDir = new File(projectDir, "build")
	File outputDocDir = new File(buildDir, "gen-doc")
	File htmlIndexFile = new File(outputDocDir, "index.html")
	File stylesDir = new File(outputDocDir, "styles")
	File mainCssFile = new File(stylesDir, "main.css")
	
    void setUp() {
        if(buildDir.exists()) buildDir.deleteDir()
        buildDir.delete()
    }

    void tearDown() {
        setUp()
    }

    void testCommonMark() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("commonmark")
                .build()

        assertEquals(SUCCESS, result.task(":commonmark").getOutcome())
		assertTrue(outputDocDir.exists())
    	assertTrue(htmlIndexFile.exists())
		assertTrue(htmlIndexFile.getText().contains("<p>Some text</p>"))
		
    	assertTrue(mainCssFile.exists())
    }
}
