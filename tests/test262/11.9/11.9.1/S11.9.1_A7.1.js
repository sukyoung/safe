  {
    var __result1 = (new Boolean(true) == new Boolean(true)) !== false;
    var __expect1 = false;
  }
  {
    var __result2 = (new Number(1) == new Number(1)) !== false;
    var __expect2 = false;
  }
  {
    var __result3 = (new String("x") == new String("x")) !== false;
    var __expect3 = false;
  }
  {
    var __result4 = (new Object() == new Object()) !== false;
    var __expect4 = false;
  }
  x = {
    
  };
  y = x;
  {
    var __result5 = (x == y) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = (new Boolean(true) == new Number(1)) !== false;
    var __expect6 = false;
  }
  {
    var __result7 = (new Number(1) == new String("1")) !== false;
    var __expect7 = false;
  }
  {
    var __result8 = (new String("1") == new Boolean(true)) !== false;
    var __expect8 = false;
  }
  