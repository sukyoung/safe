/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
function success(readers) {
    __result1 = readers[0].closeSessions();
}

function error(err) {
}

tizen.seService.getReaders(success, error);


var __expect1 = undefined;