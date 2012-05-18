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

import com.innoq.qmqp.codec.RequestCodec;
import com.innoq.qmqp.codec.ResponseCodec;
import com.innoq.qmqp.protocol.QMQPException;
import com.innoq.qmqp.protocol.Request;
import com.innoq.qmqp.protocol.Response;
import com.innoq.qmqp.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Sends messages via QMQP over TCP to a mail transport agent that is
 * willing to queue them.
 */
public class QMQPClient implements IQMQPClient {

    private final RequestCodec reqCodec = new RequestCodec();
    private final ResponseCodec respCodec = new ResponseCodec();

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 628;

    private final String serverName;
    private final int port;

    /**
     * Sets up a client to send messages to localhost's port 628.
     */
    public QMQPClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * Sets up a client to send messages to localhost's port with the
     * given number.
     * @param port port to connect to
     */
    public QMQPClient(int port) {
        this(DEFAULT_HOST, port);
    }

    /**
     * Sets up a client to send messages to the given host's port 628.
     * @param host name of host to connect to
     */
    public QMQPClient(String host) {
        this(host, DEFAULT_PORT);
    }

    /**
     * Sets up a client to send messages to the given host and port.
     * @param host name of host to connect to
     * @param port port to connect to
     */
    public QMQPClient(String host, int port) {
        this.serverName = host;
        this.port = port;
    }

    /**
     * Sends a message for queueing.
     */
    public Response send(Request request) throws QMQPException {
        return respCodec.fromNetwork(send(reqCodec.toNetwork(request)));
    }

    private byte[] send(byte[] request) {
        Socket s = null;
        OutputStream os = null;
        InputStream is = null;
        boolean success = true;
        try {
            s = new Socket(serverName, port);
            os = s.getOutputStream();
            os.write(request);
            os.flush();
            is = s.getInputStream();
            return IOUtil.readFully(is);
        } catch (IOException ex) {
            success = false;
            if (s == null) {
                throw new QMQPException("Failed to connect to " + serverName
                                        + ":" + port, ex);
            }
            if (is == null) {
                throw new QMQPException("Failed to write to " + serverName
                                        + ":" + port, ex);
            }
            throw new QMQPException("Failed to read from " + serverName
                                    + ":" + port, ex);
        } finally {
            IOUtil.close(is, !success);
            IOUtil.close(os, !success);
            IOUtil.close(s, !success);
        }
    }

}
