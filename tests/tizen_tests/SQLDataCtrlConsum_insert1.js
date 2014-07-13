/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
function successcb(id, insertrowid){
  __result1 = id;
  __result2 = insertrowid;
}
function errorcb(id, error){
  __result3 = id;
  __result4 = error.name;
}
var rowData = {
     columns : ["WORD", "WORD_DESC"] ,
     values  : ["'tizen1'", "'samsung platform'"]
};
var globalSQLConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "SQL");
globalSQLConsumer.insert(1, rowData, successcb, errorcb);


var __expect1 = 1;
var __expect2 = 1;
var __expect3 = 1;
var __expect4 = "InvalidValuesError"
