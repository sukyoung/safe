/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;

function errorCB(err) {
  __result2 = 2;
 }

 function getDirectoriesCB(directories) {
   __result1 = directories[0].id;
 }

 tizen.content.getDirectories(getDirectoriesCB, errorCB);


var __expect1 = "A";
var __expect2 = 2;