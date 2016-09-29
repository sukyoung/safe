  var __reg = new RegExp("abc");
  __reg.toLocaleUpperCase = String.prototype.toLocaleUpperCase;
  {
    var __result1 = __reg.toLocaleUpperCase() !== "/ABC/";
    var __expect1 = false;
  }
  