package org.jpragey.cmarkplugin

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.List

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.commonmark.node.*
import org.commonmark.parser.Parser
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.renderer.html.HtmlRenderer
import org.gradle.api.GradleException

import groovy.lang.Closure

import org.commonmark.Extension

class CommonMarkWorker {
	
	Logger logger = LoggerFactory.getLogger("org.jpragey.cmarkplugin")
	
	Closure<String> htmlBuilder = null
	
	File sourceDir
	File genDir
	
	List<Extension> cmarkExtensions = new ArrayList<Extension>();
	
		
	public CommonMarkWorker(Closure<String> htmlBuilder, File sourceDir, File genDir, List<Extension>  cmarkExtensions) 
	{
		this.htmlBuilder = htmlBuilder;
		this.sourceDir = sourceDir
		this.genDir = genDir
		this.cmarkExtensions = cmarkExtensions
	}

	def compileMarkdown(File markdownFile, String mdFileNameRoot, File targetDir, int level) {
		logger.debug("Compiling Markdown file ${markdownFile}")
		
		File targetFile = new File(targetDir, mdFileNameRoot + '.html')
		
		try {
			Reader reader = new InputStreamReader(new FileInputStream(markdownFile), "UTF-8")
			
			Parser parser = Parser.builder()
				.extensions(cmarkExtensions)
				.build();
			Node document = parser.parseReader(reader);
			
			HtmlRenderer renderer = HtmlRenderer.builder()
				.extensions(cmarkExtensions)
				.build();
				
			String html = renderer.render(document);
			
			PrintWriter writer = new PrintWriter(targetFile, "UTF-8");
			
			HtmlEnvironment env = new HtmlEnvironment() {
				String path = Collections.nCopies(level, "../").join()
				
				int getLevel() {return level;}
				String getPathToRoot() {return path;}
				String getHtmlContent() {return html;}
				
			}
			if(htmlBuilder != null) {
				String page = htmlBuilder(env)
				writer.print(page)
				
			} else {
				String prefix =
				'''\
<html>
<head/>
<body>
'''
				String suffix =
				'''\
</body>
</html>
'''
			
				writer.print(prefix)
				writer.print(html)
				writer.print(suffix)
			}
			
			writer.close()
		} catch (IOException e) {
				throw new GradleException("Error while compiling ${markdownFile}", e)
			throw e
		}
	}

	def compileDirectory(File sourceDir, File targetDir, int level) {
		logger.debug("Compiling directory ${sourceDir}")
		
		targetDir.mkdirs()
		for(File file: sourceDir.listFiles()) {
			if(file == null) {
				throw new GradleException("Can't get directory ${sourceDir.absolutePath} content.")
			}
	
			if(file.isFile()) {
				
				String fileName = file.getName();
				if(fileName.endsWith(".md")) {
					compileMarkdown(file, fileName.substring(0, fileName.length()-3), targetDir, level);
				} else {
					Files.copy(file.toPath(), new File(targetDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				
			} else if(file.isDirectory()) {
				File childTargetDir = new File(targetDir, file.name)
				compileDirectory(file, childTargetDir, level+1)
			}
		}
	}

	def action() {
		if(!sourceDir.isDirectory()) {
			throw new GradleException("${sourceDir.absolutePath} is not a directory.")
		}
		
		logger.info("Compiling doc directory ${sourceDir} to ${genDir}")

		compileDirectory(sourceDir, genDir, 0)
	}

}
