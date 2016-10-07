  {
    var __result1 = ! ("1" !== new String("1"));
    var __expect1 = false;
  }
  {
    var __result2 = ! ("1" !== true);
    var __expect2 = false;
  }
  {
    var __result3 = ! ("1" !== new Boolean("1"));
    var __expect3 = false;
  }
  {
    var __result4 = ! ("1" !== 1);
    var __expect4 = false;
  }
  {
    var __result5 = ! ("1" !== new Number("1"));
    var __expect5 = false;
  }
  {
    var __result6 = ! (new String(false) !== false);
    var __expect6 = false;
  }
  {
    var __result7 = ! (false !== "0");
    var __expect7 = false;
  }
  {
    var __result8 = ! ("0" !== new Boolean("0"));
    var __expect8 = false;
  }
  {
    var __result9 = ! (false !== 0);
    var __expect9 = false;
  }
  {
    var __result10 = ! (false !== new Number(false));
    var __expect10 = false;
  }
  {
    var __result11 = ! ("1" !== {
      valueOf : (function () 
      {
        return "1";
      })
    });
    var __expect11 = false;
  }
  