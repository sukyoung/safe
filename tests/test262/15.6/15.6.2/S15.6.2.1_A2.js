  var x1 = new Boolean(1);
  {
    var __result1 = typeof x1.constructor.prototype !== "object";
    var __expect1 = false;
  }
  var x2 = new Boolean(2);
  {
    var __result2 = ! Boolean.prototype.isPrototypeOf(x2);
    var __expect2 = false;
  }
  var x3 = new Boolean(3);
  {
    var __result3 = Boolean.prototype !== x3.constructor.prototype;
    var __expect3 = false;
  }
  