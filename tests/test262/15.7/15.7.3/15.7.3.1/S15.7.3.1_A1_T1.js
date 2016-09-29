  var x = Number.prototype;
  Number.prototype = 1;
  {
    var __result1 = Number.prototype !== x;
    var __expect1 = false;
  }
  