/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

/* Error prototype object */
__ErrProtoLoc.name = "err";
var __result1 = __ErrProtoLoc.name;
var __expect1 = "err";
	
var __result2 = delete __ErrProtoLoc.name;
var __expect2 = true;

__ErrProtoLoc.message = "msg";
var __result3 = __ErrProtoLoc.message;
var __expect3 = "msg";

var __result4 = delete __ErrProtoLoc.message;
var __expect4 = true;
var __result5 = __ErrProtoLoc.message;
var __expect5 = undefined;

/* ReferenceError prototype object */
__RefErrProtoLoc.name = "refErr";
var __result6 = __RefErrProtoLoc.name;
var __expect6 = "refErr";
	
var __result7 = delete __RefErrProtoLoc.name;
var __expect7 = true;

__RefErrProtoLoc.message = "msg";
var __result8 = __RefErrProtoLoc.message;
var __expect8 = "msg";

var __result9 = delete __RefErrProtoLoc.message;
var __expect9 = true;
var __result10 = __RefErrProtoLoc.message;
var __expect10 = undefined;


/* RangeError prototype object */
__RangeErrProtoLoc.name = "rangeErr";
var __result11 = __RangeErrProtoLoc.name;
var __expect11 = "rangeErr";
	
var __result12 = delete __RangeErrProtoLoc.name;
var __expect12 = true;

__RangeErrProtoLoc.message = "msg";
var __result13 = __RangeErrProtoLoc.message;
var __expect13 = "msg";

var __result14 = delete __RangeErrProtoLoc.message;
var __expect14 = true;
var __result15 = __RangeErrProtoLoc.message;
var __expect15 = undefined;


/* TypeError prototype object */
__TypeErrProtoLoc.name = "typeErr";
var __result16 = __TypeErrProtoLoc.name;
var __expect16 = "typeErr";
	
var __result17 = delete __TypeErrProtoLoc.name;
var __expect17 = true;

__TypeErrProtoLoc.message = "msg";
var __result18 = __TypeErrProtoLoc.message;
var __expect18 = "msg";

var __result19 = delete __TypeErrProtoLoc.message;
var __expect19 = true;
var __result20 = __TypeErrProtoLoc.message;
var __expect20 = undefined;
