/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;

function success(readers) {
    function successSession(session) {
       __result1 = session.getATR().length;
    }
    readers[0].openSession(successSession);
}

function error(err) {
}

tizen.seService.getReaders(success, error);



var __expect1 = 1;