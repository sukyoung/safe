  delete Boolean.prototype.toString;
  var obj = new Boolean();
  {
    var __result1 = obj.toString() !== "[object Boolean]";
    var __expect1 = false;
  }
  