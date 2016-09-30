  var x = new Array();
  x.getClass = Object.prototype.toString;
  {
    var __result1 = x.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  var x = new Array(0, 1, 2);
  x.getClass = Object.prototype.toString;
  {
    var __result2 = x.getClass() !== "[object " + "Array" + "]";
    var __expect2 = false;
  }
  