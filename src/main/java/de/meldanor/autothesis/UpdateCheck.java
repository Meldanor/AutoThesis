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

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class UpdateCheck {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    public static final String JSON_KEY_DATE = "date";
    public static final Charset UTF_8 = Charset.forName("UTF8");
    private final WebTarget target;

    private final String token;
    private final JsonParser jsonParser;

    private static final String LATEST_UPDATE_FILE_NAME = "latestUpdate.json";

    private final File latestUpdateFile;

    public UpdateCheck(Client client, String user, String repository, String token) {

        this.target = client.target("https://api.github.com/repos").path(user).path(repository).path("commits");
        this.token = token;
        this.latestUpdateFile = new File(LATEST_UPDATE_FILE_NAME);
        this.jsonParser = new JsonParser();
    }

    public boolean hasRepositoryUpdated() {
        try {
            LocalDateTime latestCommitDate = getLatestCommitDate();
            LocalDateTime lastUpdateDate = getLastUpdateDate();

            boolean isAfter = latestCommitDate.isAfter(lastUpdateDate);
            if (isAfter)
                updateUpdateDate(latestCommitDate);

            return isAfter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private LocalDateTime getLatestCommitDate() throws Exception {
        // Send request to GitHub
        Response response = target.request().header("Authorization", "token " + token).get();
        if (Response.Status.OK.getStatusCode() != response.getStatus()) {
            throw new RuntimeException("Response is not 200! Response: " + response);
        }
        LocalDateTime latestCommitDate = LocalDateTime.MIN;
        // Extract the dates of the commits
        List<Map<String, Object>> responseList = jsonParser.parse(response.readEntity(String.class));
        for (Map<String, Object> commitInfoMap : responseList) {
            Map<String, Object> commitMap = (Map<String, Object>) commitInfoMap.get("commit");
            Map<String, Object> commiterMap = (Map<String, Object>) commitMap.get("committer");
            LocalDateTime date = LocalDateTime.parse((CharSequence) commiterMap.get(JSON_KEY_DATE), DATE_FORMAT);
            latestCommitDate = date.isAfter(latestCommitDate) ? date : latestCommitDate;
        }

        return latestCommitDate;
    }

    private LocalDateTime getLastUpdateDate() throws Exception {

        // If the file not exists, return the minimum
        if (!latestUpdateFile.exists())
            return LocalDateTime.MIN;

        String jsonContent = new String(Files.readAllBytes(latestUpdateFile.toPath()), UTF_8);
        Map<String, Object> values = jsonParser.parse(jsonContent);
        return LocalDateTime.parse((CharSequence) values.get(JSON_KEY_DATE), DATE_FORMAT);
    }

    private void updateUpdateDate(LocalDateTime newUpdateDate) throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put(JSON_KEY_DATE, DATE_FORMAT.format(newUpdateDate));
        try (BufferedWriter bWriter = Files.newBufferedWriter(latestUpdateFile.toPath(), UTF_8)) {
            bWriter.write(new JsonSerializer().serialize(map));
        }
    }
}
