/*
  Copyright (C) 2012 innoQ Deutschland GmbH

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
import org.junit.Before;
import org.junit.Test;

public class QMQPClientTest {

    private static final Random RAND = new Random();
    private QMQPTestServer server;

    @Before
    public void initializeServer() {
        server = new QMQPTestServer(RAND.nextInt(Character.MAX_VALUE - 1024)
                                    + 1024);
    }

    @After
    public void shhutdownServer() {
        server.stop();
    }

    @Test
    public void whenThingsGoWell() {
        QMQPClient client = new QMQPClient(server.getPort());
        server.handleOneRequest(new QMQPTestServer.TestRequestHandler() {
                public Response handle(Request r) {
                    return new Response(ReturnCode.OK, r.getSender());
                }
            });
        String sender = "bar@baz";
        Response res = client.send(new Request(new byte[0], sender,
                                               "foo@example.org"));
        Assert.assertEquals(ReturnCode.OK, res.getReturnCode());
        Assert.assertEquals(sender, res.getDetails());
    }

}