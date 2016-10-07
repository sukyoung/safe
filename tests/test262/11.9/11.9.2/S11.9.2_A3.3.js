  {
    var __result1 = (0 != false) !== false;
    var __expect1 = false;
  }
  {
    var __result2 = ("1" != true) !== false;
    var __expect2 = false;
  }
  {
    var __result3 = (new Boolean(false) != false) !== false;
    var __expect3 = false;
  }
  {
    var __result4 = ({
      valueOf : (function () 
      {
        return "0";
      })
    } != false) !== false;
    var __expect4 = false;
  }
  