  var obj = {
    
  };
  obj.length = 10;
  obj.reverse = Array.prototype.reverse;
  obj[0] = true;
  obj[2] = Infinity;
  obj[4] = undefined;
  obj[5] = undefined;
  obj[8] = "NaN";
  obj[9] = "-1";
  var reverse = obj.reverse();
  {
    var __result1 = reverse !== obj;
    var __expect1 = false;
  }
  {
    var __result2 = obj[0] !== "-1";
    var __expect2 = false;
  }
  {
    var __result3 = obj[1] !== "NaN";
    var __expect3 = false;
  }
  {
    var __result4 = obj[2] !== undefined;
    var __expect4 = false;
  }
  {
    var __result5 = obj[3] !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = obj[4] !== undefined;
    var __expect6 = false;
  }
  {
    var __result7 = obj[5] !== undefined;
    var __expect7 = false;
  }
  {
    var __result8 = obj[6] !== undefined;
    var __expect8 = false;
  }
  {
    var __result9 = obj[7] !== Infinity;
    var __expect9 = false;
  }
  {
    var __result10 = obj[8] !== undefined;
    var __expect10 = false;
  }
  {
    var __result11 = obj[9] !== true;
    var __expect11 = false;
  }
  obj.length = 9;
  var reverse = obj.reverse();
  {
    var __result12 = reverse !== obj;
    var __expect12 = false;
  }
  {
    var __result13 = obj[0] !== undefined;
    var __expect13 = false;
  }
  {
    var __result14 = obj[1] !== Infinity;
    var __expect14 = false;
  }
  {
    var __result15 = obj[2] !== undefined;
    var __expect15 = false;
  }
  {
    var __result16 = obj[3] !== undefined;
    var __expect16 = false;
  }
  {
    var __result17 = obj[4] !== undefined;
    var __expect17 = false;
  }
  {
    var __result18 = obj[5] !== undefined;
    var __expect18 = false;
  }
  {
    var __result19 = obj[6] !== undefined;
    var __expect19 = false;
  }
  {
    var __result20 = obj[7] !== "NaN";
    var __expect20 = false;
  }
  {
    var __result21 = obj[8] !== "-1";
    var __expect21 = false;
  }
  