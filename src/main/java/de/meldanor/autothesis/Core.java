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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Core {

    public static final Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public static void main(String[] args) {
        try {
            AutoThesisCommandOption options = AutoThesisCommandOption.getInstance();
            CommandLine commandLine = new GnuParser().parse(options, args);

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

            logger.info("Hello World, this is AutoThesis!");
            long intervalMinutes = Long.parseLong(commandLine.getOptionValue(options.getIntervalCommand(), "60"));
            logger.info("Check for update interval: " + intervalMinutes + " min");
            final AutoThesis autoThesis = new AutoThesis(user, repo, token);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                try {
                    autoThesis.execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, 0, intervalMinutes, TimeUnit.MINUTES);

        } catch (Exception e) {
            logger.throwing(Level.ERROR, e);
        }
    }
}
