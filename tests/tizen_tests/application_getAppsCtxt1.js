/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onRunningAppsContext(contexts) {
  __result1 = contexts[0].id;
 }
function error(e){
  __result2 = e.name;
}
 tizen.application.getAppsContext(onRunningAppsContext, error);

 var __expect1 = "a";
 var __expect2 = "UnknownError";
