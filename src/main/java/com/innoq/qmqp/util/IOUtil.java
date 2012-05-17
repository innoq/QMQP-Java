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

package com.innoq.qmqp.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * I/O utility functions.
 */
public abstract class IOUtil {
    private IOUtil() {}

    private static final int BUF_LEN = 8192;

    /**
     * Reads a the given stream until it is exhausted and returns the
     * content.
     */
    public static byte[] readFully(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[BUF_LEN];
        int len = 0;
        while (len >= 0) {
            len = is.read(buf, 0, BUF_LEN);
            if (len > 0) {
                bos.write(buf, 0, len);
            }
        }
        return bos.toByteArray();
    }
}


