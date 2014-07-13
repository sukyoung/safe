/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
function alertForCorruptedRemovableDrives(storages) {
    __result1 = storages.length;
    __result2 = storages[0].label;
}
function onerror(e){
    __result3 = e.name;
}

tizen.filesystem.listStorages(alertForCorruptedRemovableDrives, onerror);



var __expect1 = 1;
var __expect2 = "music";
var __expect3 = "UnknownError";