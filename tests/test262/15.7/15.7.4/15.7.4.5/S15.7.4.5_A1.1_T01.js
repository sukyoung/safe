  {
    var __result1 = Number.prototype.toFixed() !== "0";
    var __expect1 = false;
  }
  {
    var __result2 = Number.prototype.toFixed(0) !== "0";
    var __expect2 = false;
  }
  {
    var __result3 = Number.prototype.toFixed(1) !== "0.0";
    var __expect3 = false;
  }
  {
    var __result4 = Number.prototype.toFixed(1.1) !== "0.0";
    var __expect4 = false;
  }
  {
    var __result5 = Number.prototype.toFixed(0.9) !== "0";
    var __expect5 = false;
  }
  {
    var __result6 = Number.prototype.toFixed("1") !== "0.0";
    var __expect6 = false;
  }
  {
    var __result7 = Number.prototype.toFixed("1.1") !== "0.0";
    var __expect7 = false;
  }
  {
    var __result8 = Number.prototype.toFixed("0.9") !== "0";
    var __expect8 = false;
  }
  {
    var __result9 = Number.prototype.toFixed(Number.NaN) !== "0";
    var __expect9 = false;
  }
  {
    var __result10 = Number.prototype.toFixed("some string") !== "0";
    var __expect10 = false;
  }
      var __result11 = Number.prototype.toFixed(- 0.1) !== "0";
      var __expect11 = false;
  
