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

package com.innoq.qmqp.codec;

import com.innoq.qmqp.protocol.QMQPException;
import com.innoq.qmqp.protocol.Request;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Encodes a QMQP Request to its network representation.
 */
public class RequestCodec {

    private final NetStringCodec netString = new NetStringCodec();
    private final EightBitTextMessageCodec eightBit = new EightBitTextMessageCodec();
    private static final String ASCII = "ASCII";

    /**
     * Encodes a QMQP Request to its network representation.
     * @param request the request to encode, must not be null
     * @return request in its network representation
     * @throws QMQPException if the request is malformed
     */
    public byte[] toNetwork(Request r) {
        if (r == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        final ByteArrayOutputStream inner = new ByteArrayOutputStream();
        writeAsNetString(inner, eightBit.toMessage(r.getMessage()));
        try {
            writeAsNetString(inner, r.getSender().getBytes(ASCII));
            for (String s : r.getRecipients()) {
                writeAsNetString(inner, s.getBytes(ASCII));
            }
        } catch (UnsupportedEncodingException uex) {
            // plain impossible
            throw new RuntimeException("Huh, ASCII is not supported?",
                                       uex);
        }
        byte[] body = inner.toByteArray();
        return netString.toNetString(body);
    }

    private void writeAsNetString(final ByteArrayOutputStream bos,
                                  final byte[] unencoded) {
        final byte[] data = netString.toNetString(unencoded);
        bos.write(data, 0, data.length);
    }
}