package de.meldanor.autothesis;/*
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

import org.apache.commons.cli.*;

import javax.ws.rs.client.ClientBuilder;

/**
 *
 */
public class Core {

    public static void main(String[] args) throws Exception {

        AutoThesisCommandOption options = AutoThesisCommandOption.getInstance();
        CommandLine commandLine =  new GnuParser().parse(options, args);

        // Missing commands
        if (!commandLine.hasOption(options.getUserCommand()) ||
                !commandLine.hasOption(options.getTokenCommand()) ||
                !commandLine.hasOption(options.getRepoCommand())) {
            new HelpFormatter().printHelp("autothesis", options);
            return;
        }

        String user = commandLine.getOptionValue(options.getUserCommand());
        String repo = commandLine.getOptionValue(options.getRepoCommand());
        String token = commandLine.getOptionValue(options.getTokenCommand());

        System.out.println("Hello World, this is AutoThesis!");
        AutoThesis autoThesis = new AutoThesis(user, repo, token);
        autoThesis.execute();
    }


}
