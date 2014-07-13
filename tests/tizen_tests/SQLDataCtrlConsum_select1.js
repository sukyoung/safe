/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
function getValueSuccessCB(result, id){
  __result1 = result[0].columns[0];
  __result2 = id;
}
function errorcb(id, error){
  __result3 = id;
  __result4 = error.name;
}

var array = ["WORD", "WORD_DESC" ];
var globalSQLConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "SQL");
globalSQLConsumer.select(1, array, "WORD='tizen1'", getValueSuccessCB, errorcb);


var __expect1 = "";
var __expect2 = 1;
var __expect3 = 1;
var __expect4 = "InvalidValuesError"