  {
    var __result1 = Object(0).valueOf() !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = typeof Object(0) !== "object";
    var __expect2 = false;
  }
  {
    var __result3 = Object(0).constructor.prototype !== Number.prototype;
    var __expect3 = false;
  }
  if (Object(- 0).valueOf() !== - 0)
  {
    $ERROR('#4.1: Object(-0).valueOf() === 0. Actual: ' + (Object(- 0).valueOf()));
  }
  else
  {
    var __result4 = 1 / Object(- 0).valueOf() !== Number.NEGATIVE_INFINITY;
    var __expect4 = false;
  }
  {
    var __result5 = typeof Object(- 0) !== "object";
    var __expect5 = false;
  }
  {
    var __result6 = Object(- 0).constructor.prototype !== Number.prototype;
    var __expect6 = false;
  }
  {
    var __result7 = Object(1).valueOf() !== 1;
    var __expect7 = false;
  }
  {
    var __result8 = typeof Object(1) !== "object";
    var __expect8 = false;
  }
  {
    var __result9 = Object(1).constructor.prototype !== Number.prototype;
    var __expect9 = false;
  }
  {
    var __result10 = Object(- 1).valueOf() !== - 1;
    var __expect10 = false;
  }
  {
    var __result11 = typeof Object(- 1) !== "object";
    var __expect11 = false;
  }
  {
    var __result12 = Object(- 1).constructor.prototype !== Number.prototype;
    var __expect12 = false;
  }
  {
    var __result13 = Object(Number.MIN_VALUE).valueOf() !== Number.MIN_VALUE;
    var __expect13 = false;
  }
  {
    var __result14 = typeof Object(Number.MIN_VALUE) !== "object";
    var __expect14 = false;
  }
  {
    var __result15 = Object(Number.MIN_VALUE).constructor.prototype !== Number.prototype;
    var __expect15 = false;
  }
  {
    var __result16 = Object(Number.MAX_VALUE).valueOf() !== Number.MAX_VALUE;
    var __expect16 = false;
  }
  {
    var __result17 = typeof Object(Number.MAX_VALUE) !== "object";
    var __expect17 = false;
  }
  {
    var __result18 = Object(Number.MAX_VALUE).constructor.prototype !== Number.prototype;
    var __expect18 = false;
  }
  {
    var __result19 = Object(Number.POSITIVE_INFINITY).valueOf() !== Number.POSITIVE_INFINITY;
    var __expect19 = false;
  }
  {
    var __result20 = typeof Object(Number.POSITIVE_INFINITY) !== "object";
    var __expect20 = false;
  }
  {
    var __result21 = Object(Number.POSITIVE_INFINITY).constructor.prototype !== Number.prototype;
    var __expect21 = false;
  }
  {
    var __result22 = Object(Number.NEGATIVE_INFINITY).valueOf() !== Number.NEGATIVE_INFINITY;
    var __expect22 = false;
  }
  {
    var __result23 = typeof Object(Number.NEGATIVE_INFINITY) !== "object";
    var __expect23 = false;
  }
  {
    var __result24 = Object(Number.NEGATIVE_INFINITY).constructor.prototype !== Number.prototype;
    var __expect24 = false;
  }
  {
    var __result25 = isNaN(Object(Number.NaN).valueOf()) !== true;
    var __expect25 = false;
  }
  {
    var __result26 = typeof Object(Number.NaN) !== "object";
    var __expect26 = false;
  }
  {
    var __result27 = Object(Number.NaN).constructor.prototype !== Number.prototype;
    var __expect27 = false;
  }
  {
    var __result28 = Object(1.2345).valueOf() !== 1.2345;
    var __expect28 = false;
  }
  {
    var __result29 = typeof Object(1.2345) !== "object";
    var __expect29 = false;
  }
  {
    var __result30 = Object(1.2345).constructor.prototype !== Number.prototype;
    var __expect30 = false;
  }
  {
    var __result31 = Object(- 1.2345).valueOf() !== - 1.2345;
    var __expect31 = false;
  }
  {
    var __result32 = typeof Object(- 1.2345) !== "object";
    var __expect32 = false;
  }
  {
    var __result33 = Object(- 1.2345).constructor.prototype !== Number.prototype;
    var __expect33 = false;
  }
  