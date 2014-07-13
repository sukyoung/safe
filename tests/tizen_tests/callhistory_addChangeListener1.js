/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var onListenerCB = {
  onadded: function(newItems) {
    __result1 = newItems[0].startTime.toString();
  },
  onchanged: function(changedItems) {
    __result2 = changedItems[0].direction;
  }
};

// Registers a call history callback
var handle = tizen.callhistory.addChangeListener(onListenerCB);

var __expect1 = "";
var __expect2 = "";
var __result3 = handle;
var __expect3 = 1;

