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

import com.innoq.qmqp.protocol.QMQPException;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

    /**
     * Closes the given Closeable and swallows any exception thrown in
     * close if swallowException asks for it.
     * @throws QMQPException wrapping an IOException if close throws
     *         an exception and swallowError is false
     */
    public static void close(Closeable c, boolean swallowError)
        throws QMQPException {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                if (!swallowError) {
                    throw new QMQPException("failed to close connection", ex);
                }
                // would hide a different exception, swallow here
            }
        }
    }

    /*
     * I'd love to avoid the duplication but Socket doesn't implement
     * Closeable before Java7
     */

    /**
     * Closes the given Socket and swallows any exception thrown in
     * close if swallowException asks for it.
     * @throws QMQPException wrapping an IOException if close throws
     *         an exception and swallowError is false
     */
    public static void close(Socket c, boolean swallowError)
        throws QMQPException {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                if (!swallowError) {
                    throw new QMQPException("failed to close connection", ex);
                }
                // would hide a different exception, swallow here
            }
        }
    }

    /**
     * Closes the given Socket and swallows any exception thrown in
     * close if swallowException asks for it.
     * @throws QMQPException wrapping an IOException if close throws
     *         an exception and swallowError is false
     */
    public static void close(ServerSocket c, boolean swallowError)
        throws QMQPException {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                if (!swallowError) {
                    throw new QMQPException("failed to close connection", ex);
                }
                // would hide a different exception, swallow here
            }
        }
    }
}
