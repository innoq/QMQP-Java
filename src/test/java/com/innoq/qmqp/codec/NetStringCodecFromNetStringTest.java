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
import org.junit.Assert;
import org.junit.Test;

public class NetStringCodecFromNetStringTest {

    @Test(expected=IllegalArgumentException.class)
    public void doesntAcceptNullInput() {
        new NetStringCodec().fromNetString(null);
    }

    @Test(expected=QMQPException.class)
    public void throwsOnEmptyArray() {
        new NetStringCodec().fromNetString(new byte[0]);
    }

    @Test(expected=QMQPException.class)
    public void throwsOnMissingLength() {
        new NetStringCodec().fromNetString(new byte[] {':', ',' });
    }

    @Test(expected=QMQPException.class)
    public void throwsOnMissingColon() {
        new NetStringCodec().fromNetString(new byte[] {'1', '2' });
    }

    @Test(expected=QMQPException.class)
    public void throwsOnNonNumericLength() {
        new NetStringCodec().fromNetString(new byte[] {
                1, ':', 'a', ','
            });
    }

    @Test(expected=QMQPException.class)
    public void throwsOnLengthTooSmall() {
        new NetStringCodec().fromNetString(new byte[] {
                '0', ':', 'a', ','
            });
    }

    @Test(expected=QMQPException.class)
    public void throwsOnLengthTooBig() {
        new NetStringCodec().fromNetString(new byte[] {
                '2', ':', 'a', ','
            });
    }

    @Test(expected=QMQPException.class)
    public void throwsOnMissingTerminator() {
        new NetStringCodec().fromNetString(new byte[] {
                '1', ':', 'a', 0
            });
    }

    @Test
    public void emptyInput() {
        Assert.assertArrayEquals(new byte[0],
                                 new NetStringCodec()
                                 .fromNetString(new byte[] { '0', ':', ',' }));
    }

    @Test
    public void correctlyDecodes1Byte() {
        Assert.assertArrayEquals(new byte[] { 'a' },
                                 new NetStringCodec().fromNetString(new byte[] {
                                         '1', ':', 'a', ','
                                     }));
    }

    @Test
    public void correctlyDecodes255Byte() {
        byte[] expected = new byte[255];
        byte[] input = new byte[260];
        input[0] = '2'; input[1] = '5'; input[2] = '5';
        input[3] = ':';
        for (int i = 0; i < 255; i++) {
            expected[i] = input[i + 4] = (byte) i;
        }
        input[259] = ',';
        Assert.assertArrayEquals(expected,
                                 new NetStringCodec().fromNetString(input));
    }
}
