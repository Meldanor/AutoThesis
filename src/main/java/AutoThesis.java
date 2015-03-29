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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
public class AutoThesis implements Closeable {

    private final Client client;
    private final UpdateCheck updateCheck;

    public AutoThesis(String user, String repo, String token) throws Exception {

        this.client = ClientBuilder.newClient();
        this.updateCheck = new UpdateCheck(client, user, repo, token);
    }

    public void execute() throws Exception {
        if (updateCheck.hasRepositoryUpdated()) {
            // Do stuff
        }
    }

    @Override
    public void close() throws IOException {
        if (this.client != null)
            client.close();
    }
}
