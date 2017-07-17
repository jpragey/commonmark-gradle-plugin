package org.jpragey.gradle.commonmark

interface HtmlEnvironment {

	int getLevel();
	String getPathToRoot();
	String getHtmlContent();
}
