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
import org.junit.Assert;
import org.junit.Test;

public class ResponseCodecTest {

    @Test(expected=IllegalArgumentException.class)
    public void doesntAcceptNullInput() {
        new ResponseCodec().fromNetwork(null);
    }

    @Test
    public void parsesOk() {
        Response r = new ResponseCodec().fromNetwork(new byte[] {
                '1', '2', ':', 'K',
                'A', 'l', 'l', ' ', 'i', 's', ' ', 'f', 'i', 'n', 'e',
                ','
            });
        Assert.assertEquals(ReturnCode.OK, r.getReturnCode());
        Assert.assertEquals("All is fine", r.getDetails());
    }

    @Test
    public void parsesUtf8() throws java.io.UnsupportedEncodingException {
        Response r = new ResponseCodec().fromNetwork("3:Z\u00e4,"
                                                     .getBytes("UTF8"));
        Assert.assertEquals(ReturnCode.TEMP_FAIL, r.getReturnCode());
        Assert.assertEquals("\u00e4", r.getDetails());
    }

    @Test(expected=QMQPException.class)
    public void failsOnNonUtf8() throws java.io.UnsupportedEncodingException {
        Response r = new ResponseCodec().fromNetwork(new byte[] {
                '2', ':', 'K', (byte) 255, ','
            });
    }

    @Test
    public void encodesOk() {
        byte[] expected = new byte[] {
                '1', '2', ':', 'K',
                'A', 'l', 'l', ' ', 'i', 's', ' ', 'f', 'i', 'n', 'e',
                ','
            };
        Assert.assertArrayEquals(expected,
                                 new ResponseCodec()
                                 .toNetwork(new Response(ReturnCode.OK,
                                                         "All is fine")));
    }

}