  var x = new Array(0);
  x.getClass = Object.prototype.toString;
  {
    var __result1 = x.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  