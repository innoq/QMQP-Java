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

/**
 * Encapsulates a QMQP server response.
 */
public final class Response {

    private final ReturnCode rc;
    private String details;

    /**
     * Creates a response from a given return code and detail message.
     * @param rc the parsed return code.
     * @param details the detail message provided by the server, must
     *        not be null.
     */
    public Response(ReturnCode rc, String details) {
        if (details == null) {
            throw new IllegalArgumentException("details must not be null");
        }
        this.rc = rc;
        this.details = details;
    }

    /**
     * The parsed return code.
     */
    public ReturnCode getReturnCode() {
        return rc;
    }

    /**
     * The detail message provided by the server.
     * <p>Will not be null</p>
     */
    public String getDetails() {
        return details;
    }
}