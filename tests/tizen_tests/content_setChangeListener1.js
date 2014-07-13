/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var listener= {
    oncontentadded: function(content) {
       __result1 = content.id;
    },
    oncontentupdated: function(content) {
       __result2 = content.id;
    },
    oncontentremoved: function(id) {
       __result3 = id;
    }
};

// Registers to be notified when the content changes
tizen.content.setChangeListener(listener);



var __expect1 = "a";
var __expect2 = "a";
var __expect3 = "a";
