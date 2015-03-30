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

* -u, --user The name of the user
* -r, --repo The name of the repository
* -t, --token The token to access the repo. Must have at least permission to access the repository

All arguments are necessary, the order is not relevant.

###License
MIT