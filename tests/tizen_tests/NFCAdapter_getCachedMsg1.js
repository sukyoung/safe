/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var msg = tizen.nfc.getDefaultAdapter().getCachedMessage();

var __result1 = msg.recordCount;
var __expect1 = 0;

