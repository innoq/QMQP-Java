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

package com.innoq.qmqp.testserver;

import com.innoq.qmqp.codec.RequestCodec;
import com.innoq.qmqp.codec.ResponseCodec;
import com.innoq.qmqp.protocol.QMQPException;
import com.innoq.qmqp.protocol.Request;
import com.innoq.qmqp.protocol.Response;
import com.innoq.qmqp.protocol.ReturnCode;
import com.innoq.qmqp.util.IOUtil;
import java.io.ByteArrayOutputStream;
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

    private final int port, writeDelay;
    private volatile ServerSocket server;
    private volatile QMQPException caughtException;

    /**
     * Initializes but doesn't start the server to listen on the given port.
     */
    public QMQPTestServer(int port) {
        this(port, 0);
    }

    /**
     * Initializes but doesn't start the server to listen on the given
     * port, configures a delay before writing to the socket.
     */
    public QMQPTestServer(int port, int writeDelay) {
        this.port = port;
        this.writeDelay = writeDelay;
    }

    public int getPort() {
        return port;
    }

    /**
     * Starts the server in a separate thread which handles a single
     * request and then shuts down the server again.
     *
     * <p>Waits for the server to start before the method returns.</p>
     */
    public void handleOneRequest(TestRequestHandler handler) {
        OneRequestThread t = new OneRequestThread(handler);
        t.start();
        synchronized (t) {
            while (!t.up && caughtException == null) {
                try {
                    t.wait();
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    /**
     * Shuts down the server if it is running, swallows any exception.
     */
    public void stop() {
        IOUtil.close(server, true);
        server = null;
    }

    private final class OneRequestThread extends Thread {
        private final TestRequestHandler handler;
        private volatile boolean up;

        OneRequestThread(TestRequestHandler h) {
            if (h == null) {
                throw new QMQPException("missing handler");
            }
            this.handler = h;
        }

        private synchronized void isUp() {
            up = true;
            notifyAll();
        }

        private synchronized void caughtException(QMQPException ex) {
            caughtException = ex;
            notifyAll();
        }

        public void run() {
            try {
                Socket client = null;
                InputStream in = null;
                OutputStream out = null;
                try {
                    server = new ServerSocket(port);
                    isUp();
                    client = server.accept();
                    in = client.getInputStream();
                    out = client.getOutputStream();
                    ResponseCodec res = new ResponseCodec();
                    try {
                        Request req = readFully(in);
                        Response response = handler.handle(req);
                        if (writeDelay > 0) {
                            Thread.sleep(writeDelay);
                        }
                        out.write(res.toNetwork(response));
                    } catch (QMQPException q) {
                        out.write(res.toNetwork(new Response(ReturnCode.PERM_FAIL,
                                                             q.getMessage())));
                    }
                } catch (IOException ex) {
                    throw new QMQPException("exception in server", ex);
                } catch (InterruptedException ex) {
                    throw new QMQPException("exception in server", ex);
                } finally {
                    up = false;
                    IOUtil.close(in, true);
                    IOUtil.close(out, true);
                    IOUtil.close(client, true);
                    stop();
                }
            } catch (QMQPException ex) {
                caughtException(ex);
            }
        }
    }

    private static final int BUF_LEN = 8192;

    private Request readFully(InputStream is) throws IOException {
        RequestCodec req = new RequestCodec();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[BUF_LEN];
        byte[] bytesSoFar = null;
        int len = 0;
        while (len >= 0) {
            len = is.read(buf, 0, BUF_LEN);
            if (len > 0) {
                bos.write(buf, 0, len);
                bytesSoFar = bos.toByteArray();
                try  {
                    return req.fromNetwork(bytesSoFar);
                } catch (QMQPException ex) {
                    bos = new ByteArrayOutputStream();
                    bos.write(bytesSoFar, 0, bytesSoFar.length);
                }
            }
        }
        throw new QMQPException("no request");
    }
}
