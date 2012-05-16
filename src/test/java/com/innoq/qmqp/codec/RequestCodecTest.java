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

import com.innoq.qmqp.protocol.Request;
import org.junit.Assert;
import org.junit.Test;

public class RequestCodecTest {

    @Test(expected=IllegalArgumentException.class)
    public void doesntAcceptNullInput() {
        new RequestCodec().toNetwork(null);
    }

    @Test
    public void testRequest() throws java.io.UnsupportedEncodingException {
        String body = "Hi Bob,\n\n"
            + "so happy to meet you again.\n\n"
            + "Yours\n\n"
            + "        Alice\n\n";
        String sender = "alice@example.org";
        String recipient = "bob@example.org";
        Request r = new Request(body.getBytes("ASCII"), sender, recipient);
        String expected = (body.length() + sender.length() + recipient.length()
                           + 2 * 3 /* length for each string */
                           + 2 * 3 /* colon and comma for each string */)
            + ":" + body.length() + ":" + body + ","
            + sender.length() + ":" + sender + ","
            + recipient.length() + ":" + recipient + ",,";
        Assert.assertEquals(expected,
                            new String(new RequestCodec().toNetwork(r), "ASCII"));
    }

}