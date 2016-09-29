  Error.prototype.toString = Object.prototype.toString;
  var err1 = Error();
  {
    var __result1 = err1.toString() !== '[object ' + 'Error' + ']';
    var __expect1 = false;
  }
  