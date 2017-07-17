package org.jpragey.gradle.commonmark
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestNestedSources extends GroovyTestCase {
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

	File projectDir = new File(System.getProperty("user.dir") + "/testProjects/nestedSourcesProject")
	File buildDir = new File(projectDir, "build")

    void setUp() {
        if(buildDir.exists()) buildDir.deleteDir()
        buildDir.delete()
    }

    void tearDown() {
        setUp()
    }
	
	void testNestedSources() {
		File outputDocDir = new File(buildDir, "gen-doc")
		File htmlIndexFile = new File(outputDocDir, "index.html")
		File stylesDir = new File(outputDocDir, "styles")
		File mainCssFile = new File(stylesDir, "main.css")

		File dir0Dir = new File(outputDocDir, "dir0")
		File index0HtmlFile = new File(dir0Dir, "index0.html")
		File dir1Dir = new File(dir0Dir, "dir1")
		File index1HtmlFile = new File(dir1Dir, "index1.html")
		
		def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("commonmark")
                .build()

        assertEquals(SUCCESS, result.task(":commonmark").getOutcome())
		assertTrue(outputDocDir.exists())
		
    	assertTrue(htmlIndexFile.exists())
//		log.info("***********************************" + htmlIndexFile.getText())
		assertTrue(htmlIndexFile.getText().contains('<html>'))
		assertTrue(htmlIndexFile.getText().contains('<p>Some text</p>'))
		assertTrue(htmlIndexFile.getText().contains('''"resources/main.css'''))
		
		
		
    	assertTrue(mainCssFile.exists())
		
		assertTrue(dir0Dir.exists())
		assertTrue(index0HtmlFile.exists())
		assertTrue(index0HtmlFile.getText().contains('''"../resources/main.css'''))

		assertTrue(dir1Dir.exists())
		assertTrue(index1HtmlFile.exists())
		assertTrue(index1HtmlFile.getText().contains('''"../../resources/main.css'''))

    }
//""
}
