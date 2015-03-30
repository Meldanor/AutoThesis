AutoThesis
======

This is a little project for my Raspberry Pi. It checks my GitHub repository of my BachelorThesis for updates and if 
there are updates, it download and compile it via LaTeX. After the compilation, if will upload the PDF as a new release
to GitHub.

###Required Programs
* PdfLatex
* MakeGlossaries
* MakeIndex
* BibTex
* Java 8
* Git

###Commandline
This programm is command line based. The arguments are used for connecting to the repository via the GitHub API v3.

* -u, --user The name of the user (required)
* -r, --repo The name of the repository (required)
* -t, --token The token to access the repo. Must have at least permission to access the repository (required)
* -i, --interval The interval in minutes between each check for updates.

The order of the argument is not relevant.
###Example
    java -jar AutoThesis.jar -u User -r UserRepo -t TokenSHA512

###License
MIT