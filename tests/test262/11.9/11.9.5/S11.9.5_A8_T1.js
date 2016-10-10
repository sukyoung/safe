  {
    var __result1 = ! (true !== new Boolean(true));
    var __expect1 = false;
  }
  {
    var __result2 = ! (true !== 1);
    var __expect2 = false;
  }
  {
    var __result3 = ! (true !== new Number(true));
    var __expect3 = false;
  }
  {
    var __result4 = ! (true !== "1");
    var __expect4 = false;
  }
  {
    var __result5 = ! (true !== new String(true));
    var __expect5 = false;
  }
  {
    var __result6 = ! (new Boolean(false) !== false);
    var __expect6 = false;
  }
  {
    var __result7 = ! (0 !== false);
    var __expect7 = false;
  }
  {
    var __result8 = ! (new Number(false) !== false);
    var __expect8 = false;
  }
  {
    var __result9 = ! ("0" !== false);
    var __expect9 = false;
  }
  {
    var __result10 = ! (false !== new String(false));
    var __expect10 = false;
  }
  {
    var __result11 = ! (true !== {
      valueOf : (function () 
      {
        return true;
      })
    });
    var __expect11 = false;
  }
  