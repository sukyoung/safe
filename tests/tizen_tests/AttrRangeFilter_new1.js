/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var filter = new tizen.AttributeRangeFilter("arrayIndex", 2, 4);

var __result1 = filter.attributeName;
var __expect1 = "arrayIndex"
var __result2 = filter.initialValue;
var __expect2 = 2
var __result3 = filter.endValue;
var __expect3 = 4