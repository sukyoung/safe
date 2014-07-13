/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var d1 = new tizen.TimeDuration(1, "DAYS");
var d2 = new tizen.TimeDuration(1, "MINS");
// Compute event1.duration - event2.duration
var a = d1.greaterThan(d2);
var __result1 = a;
var __expect1 = true;