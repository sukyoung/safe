/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
var onListenerCB = {
  onadded: function(newItems) {

  },
  onchanged: function(changedItems) {

  }
};

// Registers a call history callback
var handle = tizen.callhistory.addChangeListener(onListenerCB);

var __result1 = tizen.callhistory.removeChangeListener(handle);
var __expect1 = undefined;