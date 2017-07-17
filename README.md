# Gradle CommonMark Plugin

A [Gradle](https://gradle.org) plugin for compiling [CommonMark](http://commonmark.org) markdown files, based on it [commonmark-java implementation][commonmark-java-implementation].

It basically copies recursively a source directory (`doc` by default) to an output directory (`$buildDir/gen-doc` by default), translating 
on-the-fly files which name end with '.md'.

## Quickstart

In your gradle-based project, create a `doc` directory (for markdown and resource files), and create (for example) a `doc/README.md` 
file containing:  

``` Markdown
    Some *highlighted* text
```

In build.gradle, add:

``` groovy

buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }
    dependencies {
        classpath 'org.jpragey.gradle.commonmark:commonmark-gradle-plugin:0.1.0'
    }
}
apply plugin: 'commonmark'
 
```

Now run:

``` bash
gradle commonmark
```

and you get a beautiful `build/gen-doc/README.html` file. 

## <a name="extensions" id="extensions"></a>Markdown extensions

The Commonmark plugin accepts extensions from [commonmark-java implementation](https://github.com/atlassian/commonmark-java#extensions), 
by its `cmarkExtensions` property.
 

``` groovy

commonmark {
    cmarkExtensions = Arrays.asList(
        org.commonmark.ext.gfm.tables.TablesExtension.create()
    )
}
```

## <a name="customizing"></a>Customizing output files 

The CommonMark plugin let you customize the HTML files (eg to add CSS, page header/footer, etc)
with the `htmlConfig` property. It must be an object that implements the `org.jpragey.cmarkplugin.HtmlConfig`
interface; its only method must convert a bunch of plugin-provided values (among which the markdown HTML)
to an HTML string, that will be dumped to the output HTML file.

So you are free to use whatever method you like to render the output file. Here is an example based on groovy
standard [SimpleTemplateEngine](http://docs.groovy-lang.org/latest/html/api/groovy/text/SimpleTemplateEngine.html), 
that reads a template file `doc/templates/template.html`
and replaces `$htmlContent` by the html from the markdown file, and `$pathToRoot` by an '../' sequence 
up to the root directory (see below).  

``` groovy
import org.jpragey.cmarkplugin.HtmlEnvironment;

commonmark {
    
    String templateText = new File(projectDir, "doc/templates/template.html").text;
    def template = new groovy.text.SimpleTemplateEngine()
                    .createTemplate(templateText)
    
    sourceDir= new File(projectDir, "doc/src")

    htmlBuilder = { HtmlEnvironment env ->
        return template
                .make([
                    "pathToRoot":env.pathToRoot,
                    "htmlContent":env.htmlContent])
                .toString();
    }
}
```
Template file `doc/templates/template.html`

``` html
<html>
<head>
    <link rel="stylesheet" href="${pathToRoot}resources/main.css">
</head>
<body>
$htmlContent
</body>
</html>
```

This example is self-explaining, except for the `pathToRoot` property, which deserves some more attention.
When a project get large, its documentation get also large, so it can span several level of subdirectories.
If a doc file refers to a fixed position size, for example a CSS stylesheet, it typically uses an absolute
path (eg '/resources/main.css'). That's fine on the web, but not when you read it on a hard disk, as there's 
no notion of site root there.
The `pathToRoot` property is created by the plugin to refer to the doc 'root' in a relative way. It is simply
a sequence of `level` '../', where `level` is the depth of the source file, calculated from the doc source directory.

In our example, the fixed-position CSS file is refered to by `href="${pathToRoot}resources/main.css"`; 
the groovy SimpleTemplateEngine will bind it to the plugin-provided value because of `.make(["pathToRoot":env.pathToRoot,...`.
So when it compiles some `<doc>/a/b/c.md` file, the output file href will be `href="../../resources/main.css"`,
which is exactly what we need. 

### HtmlEnvironment properties

htmlContent (String)
: the markdown input, translated to HTML.

level (int)
: depth of source file, relative to `sourceDir` directory (0 for sourceDir).

pathToRoot (String)
: contains a sequence of `level` '../'. It is useful to create path to the root directory, whatever the doc file depth.
See [customizing](#customiSzing).   



## Task properties
sourceDir (Groovy [File](http://docs.groovy-lang.org/latest/html/groovy-jdk/java/io/File.html))
: Source directory, `$projectDir/doc` by default.

outputDir (Groovy [File](http://docs.groovy-lang.org/latest/html/groovy-jdk/java/io/File.html))
: directory where the files will be copied or translated, `$buildDir/gen-doc` by default. 

htmlConfig (org.jpragey.cmarkplugin.HtmlConfig)
: output HTML customizer. If null (by default), very simple HTML pages will be created. See [customizing](#customizing).

cmarkExtensions (List<org.commonmark.Extension>)
: List of java-commonmark extension (eg if you need tables). See [extensions](#extensions).



[commonmark-java-implementation]: https://github.com/atlassian/commonmark-java
[groovy.text.SimpleTemplateEngine]: http://docs.groovy-lang.org/latest/html/api/groovy/text/SimpleTemplateEngine.html
