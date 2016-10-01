  Array.prototype[0] = - 1;
  var x = [];
  x.length = 1;
  var arr = x.splice(0, 1);
  {
    var __result1 = arr.length !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = arr[0] !== - 1;
    var __expect2 = false;
  }
  delete arr[0];
  {
    var __result3 = arr[0] !== - 1;
    var __expect3 = false;
  }
  {
    var __result4 = x.length !== 0;
    var __expect4 = false;
  }
  {
    var __result5 = x[0] !== - 1;
    var __expect5 = false;
  }
  Object.prototype[0] = - 1;
  Object.prototype.length = 1;
  Object.prototype.splice = Array.prototype.splice;
  x = {
    
  };
  var arr = x.splice(0, 1);
  {
    var __result6 = arr.length !== 1;
    var __expect6 = false;
  }
  {
    var __result7 = arr[0] !== - 1;
    var __expect7 = false;
  }
  delete arr[0];
  {
    var __result8 = arr[0] !== - 1;
    var __expect8 = false;
  }
  {
    var __result9 = x.length !== 0;
    var __expect9 = false;
  }
  {
    var __result10 = x[0] !== - 1;
    var __expect10 = false;
  }
  