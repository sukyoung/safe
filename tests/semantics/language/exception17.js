/* Error prototype object */
@ErrProto.name = "err";
var __result1 = @ErrProto.name;
var __expect1 = "err";
	
var __result2 = delete @ErrProto.name;
var __expect2 = true;

@ErrProto.message = "msg";
var __result3 = @ErrProto.message;
var __expect3 = "msg";

var __result4 = delete @ErrProto.message;
var __expect4 = true;
var __result5 = @ErrProto.message;
var __expect5 = undefined;

/* ReferenceError prototype object */
@RefErrProto.name = "refErr";
var __result6 = @RefErrProto.name;
var __expect6 = "refErr";
	
var __result7 = delete @RefErrProto.name;
var __expect7 = true;

@RefErrProto.message = "msg";
var __result8 = @RefErrProto.message;
var __expect8 = "msg";

var __result9 = delete @RefErrProto.message;
var __expect9 = true;
var __result10 = @RefErrProto.message;
var __expect10 = undefined;


/* RangeError prototype object */
@RangeErrProto.name = "rangeErr";
var __result11 = @RangeErrProto.name;
var __expect11 = "rangeErr";
	
var __result12 = delete @RangeErrProto.name;
var __expect12 = true;

@RangeErrProto.message = "msg";
var __result13 = @RangeErrProto.message;
var __expect13 = "msg";

var __result14 = delete @RangeErrProto.message;
var __expect14 = true;
var __result15 = @RangeErrProto.message;
var __expect15 = undefined;


/* TypeError prototype object */
@TypeErrProto.name = "typeErr";
var __result16 = @TypeErrProto.name;
var __expect16 = "typeErr";
	
var __result17 = delete @TypeErrProto.name;
var __expect17 = true;

@TypeErrProto.message = "msg";
var __result18 = @TypeErrProto.message;
var __expect18 = "msg";

var __result19 = delete @TypeErrProto.message;
var __expect19 = true;
var __result20 = @TypeErrProto.message;
var __expect20 = undefined;
