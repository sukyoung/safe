/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var msg = new tizen.Message("messaging.sms", {/*plainBody: "I will arrive in 10 minutes.",*/
                                 to: ["+34666666666", "+34888888888"]});

var __result1 = msg.type;
var __expect1 = "messaging.sms"
var __result2 = msg.to[0];
var __expect2 = "+34666666666"
var __result2 = msg.to[1];
var __expect2 = "+34888888888"