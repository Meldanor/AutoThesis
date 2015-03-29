/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kilian Gärtner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.meldanor.autothesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ThesisCompiler {

    private File thesisFolder = new File("thesis");

    public ThesisCompiler() throws Exception {
        if (!this.thesisFolder.exists())
            throw new FileNotFoundException("Can't find the thesis folder at './thesis'. Please add the thesis folder to this position");
    }

    public void execute() throws Exception {
        System.out.println("Begin compilation");
        // The processes to execute
        List<ProcessBuilder> processes = Arrays.asList(
                getPdfLatexBuilder(),
                getMakeIndexBuilder(),
                getMakeGlossaryBuilder(),
                getBibtexBuilder(),
                getPdfLatexBuilder(),
                getPdfLatexBuilder()
        );

        for (ProcessBuilder processBuilder : processes) {
            processBuilder.directory(thesisFolder);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        }
        System.out.println("Finished compilation!");
    }

    private ProcessBuilder getPdfLatexBuilder() {
        return new ProcessBuilder("pdflatex", "-interaction=nonstopmode", "thesis");
    }

    private ProcessBuilder getMakeIndexBuilder() {
        return new ProcessBuilder("makeindex", "thesis");
    }

    private ProcessBuilder getMakeGlossaryBuilder() {
        return new ProcessBuilder("makeglossaries", "thesis");
    }

    private ProcessBuilder getBibtexBuilder() {
        return new ProcessBuilder("bibtex", "thesis");
    }
}
