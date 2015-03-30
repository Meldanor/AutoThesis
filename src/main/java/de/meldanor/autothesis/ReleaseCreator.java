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

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ReleaseCreator {

    private final Client client;
    private final String token;

    private final WebTarget createReleaseTarget;

    private final File thesisPdfFile =  new File("thesis/thesis.pdf");

    public ReleaseCreator(Client client, String user, String repository, String token) {
        this.client = client;
        this.token = token;

        this.createReleaseTarget = client.target("https://api.github.com/repos").path(user).path(repository).path("releases");
    }

    public void execute() throws Exception {
        if (!thesisPdfFile.exists())
            throw new FileNotFoundException("The compiled thesis.pdf was not found!");

        // Create a release by creating a tag in the repo ta
        System.out.println("Create release");
        Map<String, String> postValues = new HashMap<>();
        postValues.put("tag_name", getTagName());
        String content = new JsonSerializer().serialize(postValues);
        Response response = createReleaseTarget.request().header("Authorization", "token " + token).post(Entity.entity(content, MediaType.APPLICATION_JSON_TYPE));
        if (Response.Status.CREATED.getStatusCode() != response.getStatus()) {
            throw new RuntimeException("Response of creating release was not 201! Response: " + response);
        }
        Map<String, String> responseMap = new JsonParser().parse(response.readEntity(String.class));
        String upload_url = responseMap.get("upload_url");

        // Upload the pdf as an asset
        byte[] bytes = Files.readAllBytes(thesisPdfFile.toPath());
        System.out.println("Upload release");
        response = client.target(upload_url).queryParam("name", "thesis.pdf").request().header("Authorization", "token " + token).post(Entity.entity(bytes, "application/pdf"));
        if (Response.Status.CREATED.getStatusCode() != response.getStatus()) {
            throw new RuntimeException("Response of creating release asset was not 201! Response: " + response);
        }
        System.out.println("Upload finished!");
    }

    private String getTagName() {
        LocalDateTime now = LocalDateTime.now();

        return String.valueOf(
                now.getYear()) + '-'
                + now.getMonthValue() + '-'
                + now.getDayOfMonth() + '_'
                + now.getHour() + '-'
                + now.getMinute() + '-'
                + now.getSecond();
    }
}
