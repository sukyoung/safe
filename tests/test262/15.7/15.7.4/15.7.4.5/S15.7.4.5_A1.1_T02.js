  {
    var __result1 = (new Number(1)).toFixed() !== "1";
    var __expect1 = false;
  }
  {
    var __result2 = (new Number(1)).toFixed(0) !== "1";
    var __expect2 = false;
  }
  {
    var __result3 = (new Number(1)).toFixed(1) !== "1.0";
    var __expect3 = false;
  }
  {
    var __result4 = (new Number(1)).toFixed(1.1) !== "1.0";
    var __expect4 = false;
  }
  {
    var __result5 = (new Number(1)).toFixed(0.9) !== "1";
    var __expect5 = false;
  }
  {
    var __result6 = (new Number(1)).toFixed("1") !== "1.0";
    var __expect6 = false;
  }
  {
    var __result7 = (new Number(1)).toFixed("1.1") !== "1.0";
    var __expect7 = false;
  }
  {
    var __result8 = (new Number(1)).toFixed("0.9") !== "1";
    var __expect8 = false;
  }
  {
    var __result9 = (new Number(1)).toFixed(Number.NaN) !== "1";
    var __expect9 = false;
  }
  {
    var __result10 = (new Number(1)).toFixed("some string") !== "1";
    var __expect10 = false;
  }
      var __result11 = (new Number(1)).toFixed(- 0.1) !== "1";
      var __expect11 = false;
  
