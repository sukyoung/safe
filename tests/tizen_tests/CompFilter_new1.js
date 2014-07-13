/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var firstNameFilter = new tizen.AttributeFilter("name.firstName", "CONTAINS", "Chris");
var lastNameFilter = new tizen.AttributeFilter("name.lastName", "EXACTLY", "Smith");
var filter1 = new tizen.CompositeFilter("INTERSECTION", [firstNameFilter, lastNameFilter]);
var filter2 = new tizen.CompositeFilter("INTERSECTION");

var __result1 = filter1.type;
var __expect1 = "INTERSECTION"
var __result2 = filter1.filters.length;
var __expect2 = 2
var __result3 = filter2.type;
var __expect3 = "INTERSECTION"
var __result4 = filter2.filters.length;
var __expect4 = 0