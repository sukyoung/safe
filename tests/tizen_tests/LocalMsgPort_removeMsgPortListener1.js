/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
function onreceived(data, remoteMsgPort) {
}

var localMsgPort = tizen.messageport.requestLocalMessagePort('MessagePortA');
var watchId = localMsgPort.addMessagePortListener(onreceived);

var __result1 = localMsgPort.removeMessagePortListener(watchId);
var __expect1 = undefined;
