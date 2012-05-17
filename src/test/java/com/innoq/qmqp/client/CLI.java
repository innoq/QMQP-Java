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

import com.innoq.qmqp.protocol.Request;
import com.innoq.qmqp.protocol.Response;
import com.innoq.qmqp.protocol.ReturnCode;
import com.innoq.qmqp.util.IOUtil;
import java.io.IOException;

/**
 * Rudimentary CLI client.
 *
 * <p>Expects four args on the command line, hostname and port of
 * server, sender and recipient address. Reads the message to send
 * from stdin, sends the message and prints out the response.</p>
 */
public class CLI {
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.err.println("host, port, sender and recipient are required");
            System.exit(2);
        }
        QMQPClient client = new QMQPClient(args[0], Integer.valueOf(args[1]));
        Response r = client.send(new Request(IOUtil.readFully(System.in),
                                             args[2], args[3]));
        System.out.println(r.getReturnCode() + ": " + r.getDetails());
        System.exit(r.getReturnCode() == ReturnCode.OK ? 0 : 1);
    }
}