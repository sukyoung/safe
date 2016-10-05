  var array = [[1, 2, ], [3, ], [], ];
  {
    var __result1 = typeof array !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = array instanceof Array !== true;
    var __expect2 = false;
  }
  {
    var __result3 = array.toString !== Array.prototype.toString;
    var __expect3 = false;
  }
  {
    var __result4 = array.length !== 3;
    var __expect4 = false;
  }
  var subarray = array[0];
  {
    var __result5 = typeof subarray !== "object";
    var __expect5 = false;
  }
  {
    var __result6 = subarray instanceof Array !== true;
    var __expect6 = false;
  }
  {
    var __result7 = subarray.toString !== Array.prototype.toString;
    var __expect7 = false;
  }
  {
    var __result8 = subarray.length !== 2;
    var __expect8 = false;
  }
  {
    var __result9 = subarray[0] !== 1;
    var __expect9 = false;
  }
  {
    var __result10 = subarray[1] !== 2;
    var __expect10 = false;
  }
  var subarray = array[1];
  {
    var __result11 = typeof subarray !== "object";
    var __expect11 = false;
  }
  {
    var __result12 = subarray instanceof Array !== true;
    var __expect12 = false;
  }
  {
    var __result13 = subarray.toString !== Array.prototype.toString;
    var __expect13 = false;
  }
  {
    var __result14 = subarray.length !== 1;
    var __expect14 = false;
  }
  {
    var __result15 = subarray[0] !== 3;
    var __expect15 = false;
  }
  var subarray = array[2];
  {
    var __result16 = typeof subarray !== "object";
    var __expect16 = false;
  }
  {
    var __result17 = subarray instanceof Array !== true;
    var __expect17 = false;
  }
  {
    var __result18 = subarray.toString !== Array.prototype.toString;
    var __expect18 = false;
  }
  {
    var __result19 = subarray.length !== 0;
    var __expect19 = false;
  }
  {
    var __result20 = array[0][0] !== 1;
    var __expect20 = false;
  }
  {
    var __result21 = array[0][1] !== 2;
    var __expect21 = false;
  }
  {
    var __result22 = array[1][0] !== 3;
    var __expect22 = false;
  }
  