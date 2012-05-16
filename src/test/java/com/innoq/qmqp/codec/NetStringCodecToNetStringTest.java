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

import org.junit.Assert;
import org.junit.Test;

public class NetStringCodecToNetStringTest {

    @Test(expected=IllegalArgumentException.class)
    public void doesntAcceptNullInput() {
        new NetStringCodec().toNetString(null);
    }

    @Test
    public void emptyInput() {
        Assert.assertArrayEquals(new byte[] { '0', ':', ',' },
                                 new NetStringCodec().toNetString(new byte[0]));
    }

    @Test
    public void correctlyEncodes1Byte() {
        Assert.assertArrayEquals(new byte[] { '1', ':', 'a', ',' },
                                 new NetStringCodec().toNetString(new byte[] {
                                         'a'
                                     }));
    }

    @Test
    public void correctlyEncodes255Byte() {
        byte[] input = new byte[255];
        byte[] expected = new byte[260];
        expected[0] = '2'; expected[1] = '5'; expected[2] = '5';
        expected[3] = ':';
        for (int i = 0; i < 255; i++) {
            input[i] = expected[i + 4] = (byte) i;
        }
        expected[259] = ',';
        Assert.assertArrayEquals(expected,
                                 new NetStringCodec().toNetString(input));
    }
}
