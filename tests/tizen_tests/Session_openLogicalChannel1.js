/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

function success(readers) {
    function successChannel(channel) {
       __result1 = channel.isBasicChannel;
    }
    function errorChannel(err) {
       __result2 = err.name;
    }

    function successSession(session) {
       session.openLogicalChannel([0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe], successChannel, errorChannel);
    }
    function errorSession(err) {

    }
    readers[0].openSession(successSession, errorSession);
}

function error(err) {
}

tizen.seService.getReaders(success, error);



var __expect1 = false;
var __expect2 = "UnknownError";