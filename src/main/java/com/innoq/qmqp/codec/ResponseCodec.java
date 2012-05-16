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
import com.innoq.qmqp.protocol.Response;
import com.innoq.qmqp.protocol.ReturnCode;
import java.io.UnsupportedEncodingException;

/**
 * Decodes a QMQP Response from its network representation.
 */
public class ResponseCodec {

    private final NetStringCodec netString = new NetStringCodec();
    private static final String UTF8 = "UTF8";

    /**
     * Decodes a QMQP Response from its network representation.
     * @param response the netstring to decode, must not be null
     * @return the contained response, will not be null
     * @throws QMQPException if the netstring is malformed
     */
    public Response fromNetwork(byte[] response) throws QMQPException {
        try {
            String message =
                new String (netString.fromNetString(response), UTF8);
            return new Response(ReturnCode.fromCode(message.charAt(0)),
                                message.substring(1));
        } catch (UnsupportedEncodingException uex) {
            // plain impossible
            throw new RuntimeException("Huh, UTF-8 is not supported?",
                                       uex);
        }
    }

}
