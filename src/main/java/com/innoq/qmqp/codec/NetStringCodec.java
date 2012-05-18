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
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Codec for NetStrings as defined by http://cr.yp.to/proto/netstrings.txt
 *
 * <p>Convenience methods may be added later.</p>
 */
class NetStringCodec {

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
        NetStringResult result = readNetString(netstring, 0);
        if (netstring.length != result.endOffset) {
            throw new QMQPException("Length mismatch in netstring");
        }
        return result.data;
    }

    /**
     * Assumes the input consists of concatenated netstrings, splits
     * them and returns the decoded "interpretations".
     * @param netstrings the netstrings to decode, must not be null
     * @return the interpretations of the netstrings, will not be null
     * @throws QMQPException if any of the netstrings is malformed
     */
    public byte[][] splitNetStrings(byte[] netstrings) throws QMQPException {
        if (null == netstrings) {
            throw new IllegalArgumentException("input must not be null");
        }
        List<byte[]> result = new ArrayList<byte[]>();
        final int len = netstrings.length;
        int nextOffset = 0;
        while (nextOffset < len) {
            NetStringResult oneString = readNetString(netstrings, nextOffset);
            result.add(oneString.data);
            nextOffset = oneString.endOffset;
        }
        return result.toArray(new byte[0][]);
    }

    private int digitsInNumber(int number) {
        return number == 0 ? 1 : ((int) (Math.log(number) / LN10) + 1);
    }

    /**
     * Decodes a netstring that is contained inside given input and
     * starts at index startOffset)
     * @param input data containing the netstring
     * @param startOffset start-index of the netstriung to decode
     *        inside input
     * @return the interpretation of the netstring and the index of
     *         the first byte after the decoded netstring.
     */
    private NetStringResult readNetString(byte[] input, int startOffset) {
        if (null == input) {
            throw new IllegalArgumentException("input must not be null");
        }

        final int inputLength = input.length;
        /* minimal netsting is "0:," */
        if (inputLength - startOffset < 3) {
            throw new QMQPException("netstring too small");
        }

        final NetStringInfo info = parseLength(input, startOffset);
        if (input[info.dataStart - 1] != COLON) {
            throw new QMQPException("Missing colon in netstring");
        }
        final int comma = info.dataStart + info.dataLength;
        if (inputLength <= comma) {
            throw new QMQPException("Length mismatch in netstring");
        }
        if (input[comma] != COMMA) {
            throw new QMQPException("Missing comma in netstring");
        }

        byte[] result = new byte[info.dataLength];
        if (info.dataLength > 0) {
            System.arraycopy(input, info.dataStart, result, 0, info.dataLength);
        }
        return new NetStringResult(result, comma + 1);
    }

    private NetStringInfo parseLength(byte[] netstring, int startOffset)
        throws QMQPException {
        final int len = netstring.length;
        int accu = 0, i = startOffset;
        for (; i < len && netstring[i] >= '0' && netstring[i] <= '9'; i++) {
            accu = accu * 10 + (netstring[i] - '0');
        }
        if (i == netstring.length) {
            throw new QMQPException("missing length");
        }
        return new NetStringInfo(i + 1, accu);
    }

    private static final class NetStringResult {
        private final byte[] data;
        private final int endOffset;
        NetStringResult(byte[] result, int end) {
            data = result;
            endOffset = end;
        }
    }
    private static final class NetStringInfo {
        private final int dataStart;
        private final int dataLength;
        NetStringInfo(int start, int length) {
            dataStart = start;
            dataLength = length;
        }
    }
}
