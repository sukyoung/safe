/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var contGrp1 = new tizen.ContactGroup();
var contGrp2 = new tizen.ContactGroup('Family', 'file://opt/media/Downloads/ring.mp3');

var __result1 = contGrp1.id;
var __expect1 = null
var __result2 = contGrp1.addressBookId;
var __expect2 = null
var __result3 = contGrp1.name;
var __expect3 = "undefined"
var __result4 = contGrp1.ringtoneURI;
var __expect4 = null
var __result5 = contGrp1.photoURI;
var __expect5 = null
var __result6 = contGrp1.readOnly;
var __expect6 = false

var __result7 = contGrp2.id;
var __expect7 = null
var __result8 = contGrp2.addressBookId;
var __expect8 = null
var __result9 = contGrp2.name;
var __expect9 = "Family"
var __result10 = contGrp2.ringtoneURI;
var __expect10 = "file://opt/media/Downloads/ring.mp3"
var __result11 = contGrp2.photoURI;
var __expect11 = null
var __result12 = contGrp2.readOnly;
var __expect12 = false
