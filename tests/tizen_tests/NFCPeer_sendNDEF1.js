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
     var msg = new tizen.NDEFMessage([new tizen.NDEFRecordURI("http://www.samsungmobile.com/")]);
     nfcPeer.sendNDEF(msg, function(){
       __result1 = 1;
     }, function(err){
       __result2 = err.name;
     });
   },
   ondetach : function() {
   }
};

adapt.setPeerListener(onSuccessCB);


var __expect1 = 1;
var __expect2 = "ServiceNotAvailableError";
