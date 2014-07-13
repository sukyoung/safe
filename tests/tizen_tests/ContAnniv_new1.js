/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var date = new Date(2013, 6, 12);
var contAnniv1 = new tizen.ContactAnniversary(date);
var contAnniv2 = new tizen.ContactAnniversary(date, "a");

var __result1 = contAnniv1.date.toString();
var __expect1 = "Fri Jul 12 2013 00:00:00 GMT+0900 (...)"
var __result2 = contAnniv1.label;
var __expect2 = null

var __result3 = contAnniv2.date.toString();
var __expect3 = "Fri Jul 12 2013 00:00:00 GMT+0900 (...)"
var __result4 = contAnniv2.label;
var __expect4 = "a"
