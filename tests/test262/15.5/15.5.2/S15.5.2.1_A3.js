  var __str__obj = new String("seamaid");
  __str__obj.toString = Object.prototype.toString;
  {
    var __result1 = __str__obj.toString() !== "[object " + "String" + "]";
    var __expect1 = false;
  }
  