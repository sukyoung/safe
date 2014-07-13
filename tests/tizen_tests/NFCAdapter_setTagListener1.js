/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapt = tizen.nfc.getDefaultAdapter();

var onSuccessCB = {
   onattach : function(nfcTag) {
     __result1 = nfcTag.type;
   },
   ondetach : function() {
     __result2 = 2;
   }
};

adapt.setTagListener(onSuccessCB, ["GENERIC_TARGET"]);


var __expect1 = "GENERIC_TARGET";
var __expect2 = 2
