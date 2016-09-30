  var x = new Array();
  var y = new Array(0, 1);
  var z = new Array(2, 3, 4);
  var arr = x.concat(y, z);
  arr.getClass = Object.prototype.toString;
  {
    var __result1 = arr.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  {
    var __result2 = arr[0] !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = arr[1] !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = arr[2] !== 2;
    var __expect4 = false;
  }
  {
    var __result5 = arr[3] !== 3;
    var __expect5 = false;
  }
  {
    var __result6 = arr[4] !== 4;
    var __expect6 = false;
  }
  {
    var __result7 = arr.length !== 5;
    var __expect7 = false;
  }
  