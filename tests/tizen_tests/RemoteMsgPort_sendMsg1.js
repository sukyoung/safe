/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var remoteMsgPort = tizen.messageport.requestRemoteMessagePort('6xaeuflskd.App1', 'MessagePortB');

var __result1 = remoteMsgPort.messagePortName;
var __expect1 = "MessagePortB"
var __result2 = remoteMsgPort.appId;
var __expect2 = "6xaeuflskd.App1"
var __result3 = remoteMsgPort.isTrusted;
var __expect3 = true

remoteMsgPort.sendMessage([{key:'RESULT', value:'OK'}]);


var __result4 = remoteMsgPort.messagePortName;
var __expect4 = "MessagePortB"
var __result5 = remoteMsgPort.appId;
var __expect5 = "6xaeuflskd.App1"
var __result6 = remoteMsgPort.isTrusted;
var __expect6 = true