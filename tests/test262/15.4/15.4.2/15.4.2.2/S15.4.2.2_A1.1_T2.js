  Array.prototype.toString = Object.prototype.toString;
  var x = new Array(0);
  {
    var __result1 = x.toString() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  