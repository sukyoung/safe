/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
function getValueSuccessCB(result, id)
 {
    __result1 = result.length;
    __result2 = id;
 }

 function errorcb(id, error)
 {
    __result3 = id;
    __result4 = error.name;
 }
 var globalMappedConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "MAP");
 globalMappedConsumer.getValue(3, "tizen", getValueSuccessCB, errorcb);

var __expect1 = 1;
var __expect2 = 3;
var __expect3 = 3;
var __expect4 = "error";