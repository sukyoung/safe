  var __reg = new RegExp("abc");
  __reg.toUpperCase = String.prototype.toUpperCase;
  {
    var __result1 = __reg.toUpperCase() !== "/ABC/";
    var __expect1 = false;
  }
  