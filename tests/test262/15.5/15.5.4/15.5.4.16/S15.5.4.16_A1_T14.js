  var __reg = new RegExp("ABC");
  __reg.toLowerCase = String.prototype.toLowerCase;
  {
    var __result1 = __reg.toLowerCase() !== "/abc/";
    var __expect1 = false;
  }
  