jQCCounter
==========

A tool to see the total lines of code across multiple projects

![preview](https://raw.githubusercontent.com/Mikescher/jQCCounter/master/data/preview.png)

###Features:

 - portable on-jar program
 - Show LOC of all projects in directory
 - Group projects into project sets (e.g. Visual Studio Solutions / Multi-Project gradle files)
 - Show Language distribution (similiar to github ...)
 - List **TODO**-comments in sourcecode files (via customizable Regex)
 - Show git informations (remote, branch, commits, commits ahead, commits behind, ...)

###[> Releases](https://github.com/Mikescher/jQCCounter/releases)

###Included Language support:

 - Java (Eclipse, IntelliJ, AndroidStudio)
 - Properties
 - C#
 - C
 - C++
 - PHP
 - HTML
 - javascript
 - Textfunge
 - Delphi
 - LaTeX
 - Groovy
 - Python
 - Go
 - Rust
 - Everything IntelliJ

###Multifolder config

By default jQCCounter searches in the current folder. But you can place a jQCCounter.cfg file beside the jar to supply it with multiple folders.
The config syntax looks like this:
~~~
C|.\C\
C++|.\C++\
C#|.\C-Sharp\
Java Eclipse|.\Java\workspace\
Perl|.\Perl\
PHP|.\PHP\
Python|.\Python\
~~~

###Ignored folders

Most rules are hard-coded in the jar (PR's welcome) but you can also exclude a folder by placing a `.qcignore` file in it
