/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapt = tizen.nfc.getDefaultAdapter();

var onSuccessCB = {
   onattach : function(nfcPeer) {
     nfcPeer.setReceiveNDEFListener(
         function(message){
           __result1 = message.recordCount;
         });
   },
   ondetach : function() {
     __result2 = 2;
   }
};

adapt.setPeerListener(onSuccessCB);


var __expect1 = 1;
var __expect2 = 2;
