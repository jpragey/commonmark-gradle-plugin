package org.jpragey.cmarkplugin

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.List

import org.commonmark.node.*
import org.commonmark.parser.Parser
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.renderer.html.HtmlRenderer

import groovy.lang.Closure

import org.commonmark.Extension

class CommonMarkWorker {
	
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
			// TODO
		}
	}

	def compileDirectory(File sourceDir, File targetDir, int level) {
		targetDir.mkdirs()
		for(File file: sourceDir.listFiles()) {
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
		compileDirectory(sourceDir, genDir, 0)
	}

}
