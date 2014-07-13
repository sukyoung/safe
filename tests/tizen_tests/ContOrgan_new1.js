/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var contOrgan1 = new tizen.ContactOrganization();
var contOrgan2 = new tizen.ContactOrganization({name: "Intel", role: "SW Engineer"});

var __result1 = contOrgan1.name;
var __expect1 = null
var __result2 = contOrgan1.department;
var __expect2 = null
var __result3 = contOrgan1.title;
var __expect3 = null
var __result4 = contOrgan1.role;
var __expect4 = null
var __result5 = contOrgan1.logoURI;
var __expect5 = null

var __result6 = contOrgan2.name;
var __expect6 = "Intel"
var __result7 = contOrgan2.department;
var __expect7 = null
var __result8 = contOrgan2.title;
var __expect8 = null
var __result9 = contOrgan2.role;
var __expect9 = "SW Engineer"
var __result10 = contOrgan2.logoURI;
var __expect10 = null
