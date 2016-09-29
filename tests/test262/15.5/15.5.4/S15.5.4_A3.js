  {
    var __result1 = ! (Object.prototype.isPrototypeOf(String.prototype));
    var __expect1 = false;
  }
  delete String.prototype.toString;
  {
    var __result2 = String.prototype.toString() != "[object " + "String" + "]";
    var __expect2 = false;
  }
  