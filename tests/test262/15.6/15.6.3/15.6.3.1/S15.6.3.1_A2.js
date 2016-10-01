  x = Boolean.prototype;
  Boolean.prototype = 1;
  {
    var __result1 = Boolean.prototype !== x;
    var __expect1 = false;
  }
  