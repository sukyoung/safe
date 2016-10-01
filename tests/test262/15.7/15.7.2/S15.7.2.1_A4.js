  delete Number.prototype.toString;
  var obj = new Number();
  {
    var __result1 = obj.toString() !== "[object Number]";
    var __expect1 = false;
  }
  