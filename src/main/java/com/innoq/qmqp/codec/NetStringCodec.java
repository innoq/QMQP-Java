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

import com.innoq.qmqp.client.QMQPException;
import java.lang.Math;

/**
 * Simple Codec for NetStrings as defined by http://cr.yp.to/proto/netstrings.txt
 *
 * <p>Convenience methods may be added later.</p>
 *
 * <p>This class is considered internal, its API may change at any time.</p>
 */
public class NetStringCodec {

    private static final double LN10 = Math.log(10);

    private static final byte COLON = ':';
    private static final byte COMMA = ',';

    /**
     * Creates a new byte-array containing the given bytes encoded as
     * a netstring.
     * @param orig the text to encode, must not be null
     * @return the text as netstring, will not be null
     */
    public byte[] toNetString(byte[] orig) {
        if (null == orig) {
            throw new IllegalArgumentException("input must not be null");
        }
        final int len = orig.length;
        final int exp = digitsInNumber(len);
        // (length marker takes exp + 1 bytes) + colon + original bytes + comma
        final int resLen = exp + 2 + len;
        final byte[] result = new byte[resLen];
        int remaining = len;
        for (int i = 0; i < exp; i++) {
            int digit = remaining % 10;
            result[exp - i - 1] = (byte) ('0' + digit);
            remaining /= 10;
        }
        result[exp] = COLON;
        System.arraycopy(orig, 0, result, exp + 1, len);
        result[resLen - 1] = COMMA;
        return result;
    }

    /**
     * Creates a new byte-array containing the "interpretation" of the
     * given netstring.
     * @param netstring the netstring to decode, must not be null
     * @return the interpretation of the netstring, will not be null
     * @throws QMQPException if the netstring is malformed
     */
    public byte[] fromNetString(byte[] netstring) throws QMQPException {
        if (null == netstring) {
            throw new IllegalArgumentException("input must not be null");
        }

        final int inputLength = netstring.length;
        /* minimal netsting is "0:," */
        if (inputLength < 3) {
            throw new QMQPException("netstring too small");
        }

        if (netstring[inputLength - 1] != COMMA) {
            throw new QMQPException("Missing comma in netstring");
        }
        final int len = parseLength(netstring);
        final int off = digitsInNumber(len);
        if (inputLength != off + 2 + len) {
            throw new QMQPException("Length mismatch in netstring");
        }
        if (netstring[off] != COLON) {
            throw new QMQPException("Missing colon in netstring");
        }

        byte[] result = new byte[len];
        if (len > 0) {
            System.arraycopy(netstring, off + 1 , result, 0, len);
        }
        return result;
    }

    private int digitsInNumber(int number) {
        return number == 0 ? 1 : ((int) (Math.log(number) / LN10) + 1);
    }

    private int parseLength(byte[] netstring) throws QMQPException {
        final int len = netstring.length;
        int accu = 0, i = 0;
        for (; i < len && netstring[i] >= '0' && netstring[i] <= '9'; i++) {
            accu = accu * 10 + (netstring[i] - '0');
        }
        if (i == netstring.length) {
            throw new QMQPException("missing length");
        }
        return accu;
    }
}
