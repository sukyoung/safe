  {
    var __result1 = this.x !== undefined;
    var __expect1 = false;
  }
  var object = new Object();
  {
    var __result2 = object.prop !== undefined;
    var __expect2 = false;
  }
  this.y++;
  {
    var __result3 = isNaN(y) !== true;
    var __expect3 = false;
  }
  