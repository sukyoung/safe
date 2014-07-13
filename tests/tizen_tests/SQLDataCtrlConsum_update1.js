/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
function successcb(id){
  __result1 = id;
}
function errorcb(id, error){
  __result2 = id;
  __result3 = error.name;
}
var rowData = {
     columns : ["WORD", "WORD_DESC"] ,
     values  : ["'tizen1'", "'samsung platform'"]
};
var globalSQLConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "SQL");
globalSQLConsumer.update(1, rowData, "WORD='tizen1'", successcb, errorcb);


var __expect1 = 1;
var __expect2 = 1;
var __expect3 = "InvalidValuesError"