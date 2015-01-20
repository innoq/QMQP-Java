QMQP-Java
=========

This is a Java Client library for
[QMQP](http://cr.yp.to/proto/qmqp.html), the "Quick Mail Queueing
Protocol" supported by qmail and postfix.

Usage
=====

Basic Library
-------------

The main entry point to the library is the `QMQPClient` class in the
`com.innoq.qmqp.client` package along with the classes inside the
`com.innoq.qmqp.protocol` package.  Any other class is only public as
an implementation detail.

A message to be queued consists of the message itself as `byte`s -
this is the raw message and must be understood by the mail server you
want to queue the message to - the sender's address and a list of
recipient addresses.  At least one recipient is required.  Any
address must only consist of ASCII characters, the library does
enforce this but doesn't validate the address' syntax.

In order to queue a message to a mail server you create an instance of
the `QMQPClient` class optionally specifiying the hostname and port of
the server to queue to.  It defaults to "localhost" and port 628
respectively.

`QMQPClient`'s `send` method will create a new socket connection to
the server for each call, send the request, parse the reponse and
close the connection again.  Any exception that may occur will be
wrapped in a `QMQPException` which is a non-checked
`RuntimeException`.

In its most simple form sending a message to a QMQP server will be
along the lines of

    String msg = "Subject: Hello world\n"
           + "\n"
           + "Hi there\n";
    QMQPClient client = new QMQPClient();
    Response r = client.send(new Request(msg.getBytes("ASCII"),
                                         "me@example.org", "you@example.com"));
    if (r.getReturnCode().isSuccess()) {
        System.err.println("message queued successfully");
    }

Testing
-------

To simplify mock testing with mock frameworks that can't mock concrete
classes `QMQPClient` implements an interface `IQMQPClient`.

The tests jar contains a simple command line QMQP client in
`com.innoq.qmqp.client.CLI` which expects hostname and port of the
server as well as sender and recipient addresses as command line args
and reads the message from stdin.

The tests jar also contains a very lightly tested server that may be
useful as part of unit tests.

Obtaining
---------

Currently there are only two ways to get the QMQP Client Library,
either you build it from source or you pick up the jar from a Maven
repository near you.  The releases are available from Maven Central
using the groupId `com.innoq.qmqp` and artifactId `qmqp-client`.

Known Limitations
=================

* The client relies on the server to close the connection after sending
  the response.  In particular it doesn't close the connection by
  itself even after an hour.

* If the server closes the connection before sending the response the
  client will throw an exception rather than return a temporary
  failure.

* Only response details encoded in UTF-8 are supported.

Release History
===============

0.3 Released 2013-03-26
-----------------------

Added optional connect and read timeouts to the client.

0.2 Released 2012-06-09
-----------------------

Changed parent POM to use new innoQ parent, no code changes.

0.1 Released 2012-05-21
-----------------------

First public release of the code, functionally complete.

Legal
=====

  Copyright (C) 2012-2015 innoQ Deutschland GmbH

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
