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

import org.junit.Assert;
import org.junit.Test;

public class ReturnCodeTest {

    @Test
    public void fromCodeWorksForKnownCodes() {
        Assert.assertSame(ReturnCode.OK, ReturnCode.fromCode('K'));
        Assert.assertSame(ReturnCode.TEMP_FAIL, ReturnCode.fromCode('Z'));
        Assert.assertSame(ReturnCode.PERM_FAIL, ReturnCode.fromCode('D'));
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromCodeThrowsForUnknownCodes() {
        ReturnCode.fromCode('a');
    }

}