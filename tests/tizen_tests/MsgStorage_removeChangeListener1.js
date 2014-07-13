/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;

function serviceListCB(services){
    __result1 = services[0].messageStorage.removeChangeListener(1);
}

tizen.messaging.getMessageServices("messaging.sms", serviceListCB);

var __expect1 = undefined;
