  var err1 = Error("err");
  {
    var __result1 = ! Function.prototype.isPrototypeOf(err1.constructor);
    var __expect1 = false;
  }
  {
    var __result2 = ! Function.prototype.isPrototypeOf(Error.constructor);
    var __expect2 = false;
  }
  