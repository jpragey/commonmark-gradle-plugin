package org.jpragey.gradle.commonmark

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import spock.lang.Specification

import org.commonmark.Extension
import org.jpragey.gradle.commonmark.CommonMarkWorker
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestCommonMarkWorker extends Specification {

	def testSimpleHappyPath() {

		given:
		def tempDir = File.createTempDir()
		def srcDir = new File(tempDir, "src")
		def outDir = new File(tempDir, "out")
		def markdownFile = new File(srcDir, "index.md")
		def htmlFile = new File(outDir, "index.html")

		when:
		srcDir.mkdirs()
		outDir.mkdirs()
		markdownFile.createNewFile()
		markdownFile.text = markdown

		def worker = new CommonMarkWorker(null, srcDir, outDir, new ArrayList<Extension>())
		worker.action()

		then:
		assertTrue(htmlFile.exists())
		assertTrue(htmlFile.text.contains(html))
		
		where:
		markdown              | html
		"SOME MARKDOWN TEXT"  | '<p>SOME MARKDOWN TEXT</p>\n'
	}


}
