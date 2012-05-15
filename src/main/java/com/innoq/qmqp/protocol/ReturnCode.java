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
 * The possible results sent by a QMQP server as defined in http://cr.yp.to/proto/qmqp.html
 */
public enum ReturnCode {
    /**
     * Message accepted.
     */
    OK('K'),
    /**
     * Temporary failure.
     */
    TEMP_FAIL('Z'),
    /**
     * Permanent failure.
     */
    PERM_FAIL('D');

    private final char code;

    ReturnCode(char c) {
        this.code = c;
    }

    /**
     * The code-character (K, D or Z)
     */
    public char getCode() { return code; }

    /**
     * Parses a ReturnCode from the single character code.
     */
    public static ReturnCode fromCode(char c) {
        for (ReturnCode rc : values()) {
            if (rc.getCode() == c) {
                return rc;
            }
        }
        throw new IllegalArgumentException("Unknown code '" + c + "'");
    }
}