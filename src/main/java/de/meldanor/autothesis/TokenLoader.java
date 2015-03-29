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

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TokenLoader {

    private static final Charset UTF_8 = Charset.forName("UTF8");
    private static final String TOKEN_KEY = "token";

    private final File TOKEN_FILE = new File("token.json");
    private final String token;

    public TokenLoader()throws Exception{
        if (!TOKEN_FILE.exists()) {
            writeDefaultFile();
            throw new NoTokenFileException();
        }

        this.token = readToken();
    }

    private String readToken() throws Exception {

        String jsonContent = new String(Files.readAllBytes(TOKEN_FILE.toPath()), UTF_8);
        Map<String, String> map = new JsonParser().parse(jsonContent);
        return map.get(TOKEN_KEY);
    }

    private void writeDefaultFile() throws Exception {
        try(BufferedWriter bWriter = Files.newBufferedWriter(TOKEN_FILE.toPath(), UTF_8)) {
            Map<String, String> map = new HashMap<>();
            map.put("token","REPLACE_THIS_WITH_YOUR_TOKEN");
            bWriter.write(new JsonSerializer().serialize(map));
        }
    }

    class NoTokenFileException extends Exception {

    }

    public String getToken() {
        return token;
    }
}
