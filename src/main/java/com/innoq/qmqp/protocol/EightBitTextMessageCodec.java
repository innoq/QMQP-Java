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

package com.innoq.qmqp.protocol;

import java.io.ByteArrayOutputStream;

/**
 * Simple Encoder for 8 bit text messages as defined by http://cr.yp.to/proto/8bittext.html
 *
 * <p>Convenience methods and support for decoding may be added later.</p>
 *
 * <p>This class is considered internal, its API may change at any time.</p>
 */
public class EightBitTextMessageCodec {

    private static final byte LF = 012;
    private static final byte CR = 015;

    /**
     * Transforms an array of bytes into an 8 bit text message in
     * which lines are separated by \012.
     * @param the message to encode, may be null
     * @return the original message unless it contains sequences of CR
     *         and LF which are collapsed into single LFs
     */
    public byte[] toMessage(byte[] b) {
        if (b != null) {
            boolean lastByteWasCr = false;
            final int len = b.length;
            for (int i = 0; i < len; i++) {
                if (b[i] == CR) {
                    lastByteWasCr = true;
                    continue;
                }
                if (lastByteWasCr && b[i] == LF) {
                    return encodeToMessage(b, i - 1);
                }
                lastByteWasCr = false;
            }
        }
        return b;
    }

    private byte[] encodeToMessage(byte[] b, int knownGoodChars) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (knownGoodChars > 0) {
            bos.write(b, 0, knownGoodChars);
        }
        bos.write(LF);

        boolean lastByteWasCr = false;
        final int len = b.length;
        for (int i = knownGoodChars + 2; i < len; i++) {
            if (b[i] == CR) {
                lastByteWasCr = true;
                continue;
            }
            if (lastByteWasCr && b[i] != LF) {
                bos.write(CR);
            }
            bos.write(b[i]);
            lastByteWasCr = false;
        }
        if (lastByteWasCr) {
            bos.write(CR);
        }
        return bos.toByteArray();
    }
}