/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var listener= {
    oncontentadded: function(content) {
    },
    oncontentupdated: function(content) {
    },
    oncontentremoved: function(id) {
    }
};

// Registers to be notified when the content changes
var watchId = tizen.content.setChangeListener(listener);
var __result1 = tizen.content.unsetChangeListener(watchId);
var __expect1 = undefined;
