/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result3;
var sortmode = new tizen.SortMode("person.name", "DESC");
try {
  var sortmode2 = new tizen.SortMode("person.name", 1);
  } catch (er){
     __result3 = 1;
  }

var __result1 = sortmode.attributeName;
var __expect1 = "person.name"
var __result2 = sortmode.order;
var __expect2 = "DESC"
var __expect3 = 1;
var __result4 = sortmode2;
var __expect4 = undefined;
