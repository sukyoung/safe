/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
function findCB(contents) {
   __result1 = tizen.content.update(contents[0]);
}

 tizen.content.find(findCB);

var __expect1 = undefined;
