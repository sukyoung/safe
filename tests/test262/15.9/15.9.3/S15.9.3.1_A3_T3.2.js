  Date.prototype.toString = Object.prototype.toString;
  var x1 = new Date(1899, 11, 31, 23);
  {
    var __result1 = x1.toString() !== "[object Date]";
    var __expect1 = false;
  }
  var x2 = new Date(1899, 12, 1, 0);
  {
    var __result2 = x2.toString() !== "[object Date]";
    var __expect2 = false;
  }
  var x3 = new Date(1900, 0, 1, 0);
  {
    var __result3 = x3.toString() !== "[object Date]";
    var __expect3 = false;
  }
  var x4 = new Date(1969, 11, 31, 23);
  {
    var __result4 = x4.toString() !== "[object Date]";
    var __expect4 = false;
  }
  var x5 = new Date(1969, 12, 1, 0);
  {
    var __result5 = x5.toString() !== "[object Date]";
    var __expect5 = false;
  }
  var x6 = new Date(1970, 0, 1, 0);
  {
    var __result6 = x6.toString() !== "[object Date]";
    var __expect6 = false;
  }
  var x7 = new Date(1999, 11, 31, 23);
  {
    var __result7 = x7.toString() !== "[object Date]";
    var __expect7 = false;
  }
  var x8 = new Date(1999, 12, 1, 0);
  {
    var __result8 = x8.toString() !== "[object Date]";
    var __expect8 = false;
  }
  var x9 = new Date(2000, 0, 1, 0);
  {
    var __result9 = x9.toString() !== "[object Date]";
    var __expect9 = false;
  }
  var x10 = new Date(2099, 11, 31, 23);
  {
    var __result10 = x10.toString() !== "[object Date]";
    var __expect10 = false;
  }
  var x11 = new Date(2099, 12, 1, 0);
  {
    var __result11 = x11.toString() !== "[object Date]";
    var __expect11 = false;
  }
  var x12 = new Date(2100, 0, 1, 0);
  {
    var __result12 = x12.toString() !== "[object Date]";
    var __expect12 = false;
  }
  