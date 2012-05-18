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

package com.innoq.qmqp.testserver;

import com.innoq.qmqp.codec.RequestCodec;
import com.innoq.qmqp.codec.ResponseCodec;
import com.innoq.qmqp.protocol.QMQPException;
import com.innoq.qmqp.protocol.Request;
import com.innoq.qmqp.protocol.Response;
import com.innoq.qmqp.protocol.ReturnCode;
import com.innoq.qmqp.util.IOUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Rudimentary QMQP-Server not suitable for any sort of production use
 * in any way.
 */
public class QMQPTestServer {

    /**
     * Callback invoked by the server for each request.
     */
    public static interface TestRequestHandler {
        public Response handle(Request r) throws QMQPException;
    }

    private final int port;
    private ServerSocket server;

    /**
     * Initializes but doesn't start the server to listen on the given port.
     */
    public QMQPTestServer(int port) {
        this.port = port;
    }

    /**
     * Starts the server, handles a single request and then shuts down
     * the server again.
     */
    public void handleOneRequest(TestRequestHandler handler) {
        Socket client = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            server = new ServerSocket(port);
            client = server.accept();
            if (handler == null) {
                throw new QMQPException("missing handler");
            }
            in = client.getInputStream();
            out = client.getOutputStream();
            RequestCodec req = new RequestCodec();
            ResponseCodec res = new ResponseCodec();
            try {
                out.write(res.toNetwork(handler
                                        .handle(req
                                                .fromNetwork(IOUtil
                                                             .readFully(in)))));
            } catch (QMQPException q) {
                out.write(res.toNetwork(new Response(ReturnCode.PERM_FAIL,
                                                     q.getMessage())));
            }
        } catch (IOException ex) {
            throw new QMQPException("exception in server", ex);
        } finally {
            IOUtil.close(in, true);
            IOUtil.close(out, true);
            IOUtil.close(client, true);
            stop();
        }
    }

    /**
     * Shuts down the server if it is running, swallows any exception.
     */
    public void stop() {
        IOUtil.close(server, true);
        server = null;
    }

}