/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function success(readers) {
  function successCB(session) {
      __result1 = session.isClosed;
    }
    function errorCB(err) {
      __result2 = err.name;
    }
    readers[0].openSession(successCB, errorCB);
}

function error(err) {
}

tizen.seService.getReaders(success, error);


var __expect1 = true;
var __expect2 = "IOError";