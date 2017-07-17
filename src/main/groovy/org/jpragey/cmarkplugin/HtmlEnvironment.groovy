package org.jpragey.cmarkplugin

interface HtmlEnvironment {

	int getLevel();
	String getPathToRoot();
	String getHtmlContent();
}
