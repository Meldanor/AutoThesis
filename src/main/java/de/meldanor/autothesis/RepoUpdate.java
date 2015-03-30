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

import jodd.io.FileUtil;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 */
public class RepoUpdate {

    private static final String ARCHIVE_FORMAT = "zipball";
    private static final String BRANCH_REF = "master";
    private final WebTarget archieveTarget;

    private final String token;
    private File thesisFolder = new File("thesis");

    public RepoUpdate(Client client, String user, String repository, String token) throws Exception {
        this.archieveTarget = client.target("https://api.github.com/repos")
                .path(user)
                .path(repository)
                .path(ARCHIVE_FORMAT)
                .path(BRANCH_REF)
                .property(ClientProperties.FOLLOW_REDIRECTS, true);
        this.token = token;
    }

    public void execute() throws Exception {
        Core.logger.info("Delete old content");
        // Delete old content
        if (thesisFolder.exists()) {
            FileUtil.deleteDir(thesisFolder);
        }
        thesisFolder.mkdir();

        Core.logger.info("Download new content");
        // Get the current version of the repo as a zipped entity
        Response response = this.archieveTarget.request().header("Authorization", "token " + token).get();
        if (Response.Status.OK.getStatusCode() != response.getStatus()) {
            throw new RuntimeException("Response is not 200! Response: " + response);
        }
        Core.logger.info("Unzip new content");
        // unzip the content to the folder
        InputStream content = (InputStream) response.getEntity();
        ZipInputStream zipStream = new ZipInputStream(content);
        // Skip the root of the folder(this is the sha key of the commit)
        ZipEntry entry = zipStream.getNextEntry();
        String rootName = entry.getName();
        while ((entry = zipStream.getNextEntry()) != null) {
            String entryPath = entry.getName().substring(rootName.length());

            File newFile = new File(thesisFolder, entryPath);
            if (!entry.isDirectory())
                Files.copy(zipStream, newFile.toPath());
            else {
                newFile.mkdir();
            }
        }
        zipStream.closeEntry();
        zipStream.close();
        Core.logger.info("Downloading new version of repo finished");
    }
}
