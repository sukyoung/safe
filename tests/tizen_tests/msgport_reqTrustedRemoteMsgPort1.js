/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var remoteMsgPort = tizen.messageport.requestTrustedRemoteMessagePort("a", 'MessagePortA');

var __result1 = remoteMsgPort.messagePortName;
var __expect1 = "MessagePortA"
var __result2 = remoteMsgPort.appId;
var __expect2 = "a"
var __result3 = remoteMsgPort.isTrusted;
var __expect3 = true