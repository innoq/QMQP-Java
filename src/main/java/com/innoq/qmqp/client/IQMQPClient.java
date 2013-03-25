/*
  Copyright (C) 2012-2013 innoQ Deutschland GmbH

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

package com.innoq.qmqp.client;

import com.innoq.qmqp.protocol.QMQPException;
import com.innoq.qmqp.protocol.Request;
import com.innoq.qmqp.protocol.Response;

/**
 * Interface for QMQPClient to simplify mock testing.
 */
public interface IQMQPClient {
    /**
     * Sends a message for queueing.
     */
    Response send(Request request) throws QMQPException;
    /**
     * Sets the connect timeout for the client in milliseconds.
     *
     * <p>A value &lt;= 0 means no timeout at all.</p>
     */
    void setConnectTimeout(int timeout);
    /**
     * Sets the read timeout for the client in milliseconds.
     *
     * <p>A value &lt;= 0 means no timeout at all.</p>
     */
    void setReadTimeout(int timeout);
}
