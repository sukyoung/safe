  var __reg = new RegExp("ABC");
  __reg.toLocaleLowerCase = String.prototype.toLocaleLowerCase;
  {
    var __result1 = __reg.toLocaleLowerCase() !== "/abc/";
    var __expect1 = false;
  }
  