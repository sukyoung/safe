/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var localMsgPort = tizen.messageport.requestTrustedLocalMessagePort('MessagePortA');

var __result1 = localMsgPort.messagePortName;
var __expect1 = "MessagePortA"
var __result2 = localMsgPort.isTrusted;
var __expect2 = true