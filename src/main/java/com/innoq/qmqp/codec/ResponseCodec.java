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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;

/**
 * Decodes a QMQP Response from its network representation.
 */
public class ResponseCodec {

    private static final CharsetDecoder UTF8 =
        Charset.forName("UTF-8").newDecoder();

    static {
        UTF8.onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    private final NetStringCodec netString = new NetStringCodec();

    /**
     * Decodes a QMQP Response from its network representation.
     * @param response the netstring to decode, must not be null
     * @return the contained response, will not be null
     * @throws QMQPException if the netstring is malformed
     */
    public Response fromNetwork(byte[] response) throws QMQPException {
        try {
            byte[] decodedBytes = netString.fromNetString(response);
            String message =
                UTF8.decode(ByteBuffer.wrap(decodedBytes)).toString();
            return new Response(ReturnCode.fromCode(message.charAt(0)),
                                message.substring(1));
        } catch (CharacterCodingException uex) {
            throw new QMQPException("Response wasn't encoded using UTF8", uex);
        }
    }

}
