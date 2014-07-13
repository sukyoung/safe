/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var firstNameFilter = new tizen.AttributeFilter("name.firstName", "CONTAINS", "Chris");

var __result1 = firstNameFilter.attributeName;
var __expect1 = "name.firstName"
var __result2 = firstNameFilter.matchFlag;
var __expect2 = "CONTAINS"
var __result3 = firstNameFilter.matchValue;
var __expect3 = "Chris"