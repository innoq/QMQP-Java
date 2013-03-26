/*
  Copyright (C) 2012-2013 innoQ Deutschland GmbH

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.innoq.qmqp.client;

import com.innoq.qmqp.protocol.QMQPException;
import com.innoq.qmqp.protocol.Request;
import com.innoq.qmqp.protocol.Response;
import com.innoq.qmqp.protocol.ReturnCode;
import com.innoq.qmqp.testserver.QMQPTestServer;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class QMQPClientTest {

    private static final Random RAND = new Random();
    private static final String SENDER = "bar@baz";
    private QMQPTestServer server;

    @After
    public void shutdownServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void whenThingsGoWell() {
        initializeServer(0);
        assertCorrectExecution(0);
    }

    @Test
    public void timeoutButNoDelay() {
        initializeServer(0);
        assertCorrectExecution(1000);
    }

    @Test
    public void writeDelayButNoTimeout() {
        initializeServer(3000);
        assertCorrectExecution(0);
    }

    @Test(expected = QMQPException.class)
    public void readDelayWithTimeout() {
        initializeServer(3000);
        sendRequest(1000);
    }

    private void initializeServer(int writeDelay) {
        server = new QMQPTestServer(RAND.nextInt(Character.MAX_VALUE - 1024)
                                    + 1024, writeDelay);
    }

    private void assertCorrectExecution(int readTimeout) {
        Response res = sendRequest(readTimeout);
        Assert.assertEquals(ReturnCode.OK, res.getReturnCode());
        Assert.assertEquals(SENDER, res.getDetails());
    }

    private Response sendRequest(int readTimeout) {
        QMQPClient client = new QMQPClient(server.getPort());
        client.setReadTimeout(readTimeout);
        server.handleOneRequest(new QMQPTestServer.TestRequestHandler() {
                public Response handle(Request r) {
                    return new Response(ReturnCode.OK, r.getSender());
                }
            });
        return client.send(new Request(new byte[0], SENDER, "foo@example.org"));
    }
}
