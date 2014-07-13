/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onreceived(data, remoteMsgPort) {
  __result1 = data[0].key;
  __result2 = remoteMsgPort.messagePortName;
}

var localMsgPort = tizen.messageport.requestLocalMessagePort('MessagePortA');
var watchId = localMsgPort.addMessagePortListener(onreceived);

var __expect1 = "aa"
var __expect2 = "aaaa"

var __result3 = watchId;
var __expect3 = 1

