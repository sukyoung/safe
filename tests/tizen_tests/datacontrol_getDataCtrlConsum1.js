/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var globalSQLConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "SQL");
var globalMappedConsumer = tizen.datacontrol.getDataControlConsumer("http://tizen.org/datacontrol/provider/DictionaryDataControlProvider", "Dictionary", "MAP");

var __result1 = globalSQLConsumer.type;
var __expect1 = "SQL"
var __result2 = globalSQLConsumer.providerId;
var __expect2 = "http://tizen.org/datacontrol/provider/DictionaryDataControlProvider"
var __result3 = globalSQLConsumer.dataId;
var __expect3 = "Dictionary"

var __result1 = globalMappedConsumer.type;
var __expect1 = "MAP"
var __result2 = globalMappedConsumer.providerId;
var __expect2 = "http://tizen.org/datacontrol/provider/DictionaryDataControlProvider"
var __result3 = globalMappedConsumer.dataId;
var __expect3 = "Dictionary"

