/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
var watchID;
function onStorageStateChanged(storage) {
    __result1 = tizen.filesystem.removeStorageStateChangeListener(watchID);
}

watchID = tizen.filesystem.addStorageStateChangeListener(onStorageStateChanged);


var __expect1 = undefined;
