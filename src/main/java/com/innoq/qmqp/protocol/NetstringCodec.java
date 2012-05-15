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

import java.lang.Math;

/**
 * Simple Encoder for Netstrings as defined by http://cr.yp.to/proto/netstrings.txt
 *
 * <p>Convenience methods and support for decoding may be added later.</p>
 */
public class NetstringCodec {

    private static final double LN10 = Math.log(10);

    private static final byte COLON = ':';
    private static final byte COMMA = ',';

    /**
     * Creates a new byte-array containing the given bytes encoded as
     * a netstring.
     */
    public byte[] toNetString(byte[] orig) {
        if (null == orig) {
            throw new IllegalArgumentException("input must not be null");
        }
        final int len = orig.length;
        final int exp = len == 0 ? 0 : (int) (Math.log(len) / LN10);
        // (length marker takes exp + 1 bytes) + colon + original bytes + comma
        final int resLen = exp + 3 + len;
        final byte[] result = new byte[resLen];
        int remaining = len;
        for (int i = 0; i <= exp; i++) {
            int digit = remaining % 10;
            result[exp - i] = (byte) ('0' + digit);
            remaining /= 10;
        }
        result[exp + 1] = COLON;
        System.arraycopy(orig, 0, result, exp + 2, len);
        result[resLen - 1] = COMMA;
        return result;
    }

}
