/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
function successcb(id)
 {
    __result1 = id;
 }

 function errorcb(id, error)
 {
    __result2 = id;
    __result3 = error.name;
 }
 var globalMappedConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "MAP");
 globalMappedConsumer.removeValue(3, "tizen", "samsung", successcb, errorcb);

var __expect1 = 3;
var __expect2 = 3;
var __expect3 = "error";