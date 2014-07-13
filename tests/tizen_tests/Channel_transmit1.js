/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

function success(readers) {
    function successSession(session) {
       function successChannel(channel){
           function transmitsucCB(data){
             __result1 = data[0];
           }
           function errCB(er){
             __result2 = er.name;
           }

           channel.transmit([1,2,3,4], transmitsucCB, errCB);
       }

       session.openBasicChannel([0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe], successChannel);
    }
    readers[0].openSession(successSession);
}

function error(err) {
}

tizen.seService.getReaders(success, error);



var __expect1 = 1;
var __expect2 = "SecurityError";