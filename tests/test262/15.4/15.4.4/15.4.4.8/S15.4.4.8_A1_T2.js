  var x = [];
  x[0] = true;
  x[2] = Infinity;
  x[4] = undefined;
  x[5] = undefined;
  x[8] = "NaN";
  x[9] = "-1";
  var reverse = x.reverse();
  {
    var __result1 = reverse !== x;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== "-1";
    var __expect2 = false;
  }
  {
    var __result3 = x[1] !== "NaN";
    var __expect3 = false;
  }
  {
    var __result4 = x[2] !== undefined;
    var __expect4 = false;
  }
  {
    var __result5 = x[3] !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = x[4] !== undefined;
    var __expect6 = false;
  }
  {
    var __result7 = x[5] !== undefined;
    var __expect7 = false;
  }
  {
    var __result8 = x[6] !== undefined;
    var __expect8 = false;
  }
  {
    var __result9 = x[7] !== Infinity;
    var __expect9 = false;
  }
  {
    var __result10 = x[8] !== undefined;
    var __expect10 = false;
  }
  {
    var __result11 = x[9] !== true;
    var __expect11 = false;
  }
  x.length = 9;
  var reverse = x.reverse();
  {
    var __result12 = reverse !== x;
    var __expect12 = false;
  }
  {
    var __result13 = x[0] !== undefined;
    var __expect13 = false;
  }
  {
    var __result14 = x[1] !== Infinity;
    var __expect14 = false;
  }
  {
    var __result15 = x[2] !== undefined;
    var __expect15 = false;
  }
  {
    var __result16 = x[3] !== undefined;
    var __expect16 = false;
  }
  {
    var __result17 = x[4] !== undefined;
    var __expect17 = false;
  }
  {
    var __result18 = x[5] !== undefined;
    var __expect18 = false;
  }
  {
    var __result19 = x[6] !== undefined;
    var __expect19 = false;
  }
  {
    var __result20 = x[7] !== "NaN";
    var __expect20 = false;
  }
  {
    var __result21 = x[8] !== "-1";
    var __expect21 = false;
  }
  