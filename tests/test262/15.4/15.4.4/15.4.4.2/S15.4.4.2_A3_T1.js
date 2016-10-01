  Array.prototype[1] = 1;
  var x = [0, ];
  x.length = 2;
  {
    var __result1 = x.toString() !== "0,1";
    var __expect1 = false;
  }
  