/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.nfc.getDefaultAdapter();

var onSuccessCB = {onattach :
   function(nfcTag) {
     nfcTag.readNDEF(function(msg){
       __result1 = msg.recordCount;
     }, function(err){
       __result2 = err.name;
     });
   },
   ondetach : function() {

   }
};

adapter.setTagListener(onSuccessCB);



var __expect1 = 1;
var __expect2 = "ServiceNotAvailableError";
