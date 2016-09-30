var date_1899_end = -2208988800001;
var date_1900_start = -2208988800000;
var date_1969_end = -1;
var date_1970_start = 0;
var date_1999_end = 946684799999;
var date_2000_start = 946684800000;
var date_2099_end = 4102444799999;
var date_2100_start = 4102444800000;
  Date.prototype.toString = Object.prototype.toString;
  var x1 = new Date(date_1899_end);
  {
    var __result1 = x1.toString() !== "[object Date]";
    var __expect1 = false;
  }
  var x2 = new Date(date_1900_start);
  {
    var __result2 = x2.toString() !== "[object Date]";
    var __expect2 = false;
  }
  var x3 = new Date(date_1969_end);
  {
    var __result3 = x3.toString() !== "[object Date]";
    var __expect3 = false;
  }
  var x4 = new Date(date_1970_start);
  {
    var __result4 = x4.toString() !== "[object Date]";
    var __expect4 = false;
  }
  var x5 = new Date(date_1999_end);
  {
    var __result5 = x5.toString() !== "[object Date]";
    var __expect5 = false;
  }
  var x6 = new Date(date_2000_start);
  {
    var __result6 = x6.toString() !== "[object Date]";
    var __expect6 = false;
  }
  var x7 = new Date(date_2099_end);
  {
    var __result7 = x7.toString() !== "[object Date]";
    var __expect7 = false;
  }
  var x8 = new Date(date_2100_start);
  {
    var __result8 = x8.toString() !== "[object Date]";
    var __expect8 = false;
  }
  
