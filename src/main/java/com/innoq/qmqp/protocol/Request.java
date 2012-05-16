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
 * Encapsulates a QMQP client request.
 */
public final class Request {
    private final byte[] message;
    private final String sender;
    private final String[] recipients;

    /**
     * Creates a request from a raw message, a required sender and at
     * least one recipient.
     * @param message the raw and unencoded message, must not be null.
     * @param sender the envelope sender, must not be null, should
     *        contain the bare address only, must not contain non-ASCII
     *        chars.
     * @param recipients the envelope recipient addresses, at least
     *        one is required, each should contain the bare address
     *        only, must not contain non-ASCII chars
     */
    public Request(byte[] message, String sender, String... recipients) {
        if (message == null) {
            throw new IllegalArgumentException("Message must not be null.");
        }
        if (sender == null) {
            throw new IllegalArgumentException("Sender must not be null.");
        }
        if (recipients.length == 0) {
            throw new IllegalArgumentException("At least one recipient is"
                                               + " required.");
        }
        this.message = new byte[message.length];
        System.arraycopy(message, 0, this.message, 0, message.length);
        this.sender = sender;
        this.recipients = new String[recipients.length];
        System.arraycopy(recipients, 0, this.recipients, 0, recipients.length);
    }

    /**
     * The raw and unencoded message.
     * <p>Will not be null</p>
     */
    public byte[] getMessage(){
        byte[] m = new byte[message.length];
        System.arraycopy(message, 0, m, 0, message.length);
        return m;
    }

    /**
     * The sender.
     * <p>Will not be null</p>
     */
    public String getSender() {
        return sender;
    }

    /**
     * The recipients.
     * <p>Will not be empty</p>
     */
    public String[] getRecipients() {
        String[] r = new String[recipients.length];
        System.arraycopy(recipients, 0, r, 0, recipients.length);
        return r;
    }
}