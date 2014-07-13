/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var contAddr1 = new tizen.ContactAddress({streetAddress:'Gran Via, 32', postalCode:'50013', city:'Zaragoza',
                                         country:'ES', types:['WORK']});

var __result1 = contAddr1.country;
var __expect1 = "ES"
var __result2 = contAddr1.region;
var __expect2 = null
var __result3 = contAddr1.city;
var __expect3 = "Zaragoza"
var __result4 = contAddr1.streetAddress;
var __expect4 = "Gran Via, 32"
var __result5 = contAddr1.additionalInformation;
var __expect5 = null
var __result6 = contAddr1.postalCode;
var __expect6 = "50013"
var __result7 = contAddr1.isDefault;
var __expect7 = false
var __result8 = contAddr1.types.length;
var __expect8 = 1
var __result9 = contAddr1.types[0];
var __expect9 = "WORK"