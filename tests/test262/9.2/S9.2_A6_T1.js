  {
    var __result1 = Boolean(new Object()) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = Boolean(new String("")) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Boolean(new String()) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = Boolean(new Boolean(true)) !== true;
    var __expect4 = false;
  }
  {
    var __result5 = Boolean(new Boolean(false)) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = Boolean(new Boolean()) !== true;
    var __expect6 = false;
  }
  {
    var __result7 = Boolean(new Array()) !== true;
    var __expect7 = false;
  }
  {
    var __result8 = Boolean(new Number()) !== true;
    var __expect8 = false;
  }
  {
    var __result9 = Boolean(new Number(- 0)) !== true;
    var __expect9 = false;
  }
  {
    var __result10 = Boolean(new Number(0)) !== true;
    var __expect10 = false;
  }
  {
    var __result11 = Boolean(new Number()) !== true;
    var __expect11 = false;
  }
  {
    var __result12 = Boolean(new Number(Number.NaN)) !== true;
    var __expect12 = false;
  }
  {
    var __result13 = Boolean(new Number(- 1)) !== true;
    var __expect13 = false;
  }
  {
    var __result14 = Boolean(new Number(1)) !== true;
    var __expect14 = false;
  }
  {
    var __result15 = Boolean(new Number(Number.POSITIVE_INFINITY)) !== true;
    var __expect15 = false;
  }
  {
    var __result16 = Boolean(new Number(Number.NEGATIVE_INFINITY)) !== true;
    var __expect16 = false;
  }
  {
    var __result17 = Boolean((function () 
    {
      
    })) !== true;
    var __expect17 = false;
  }
  {
    var __result18 = Boolean(new Date()) !== true;
    var __expect18 = false;
  }
  {
    var __result19 = Boolean(new Date(0)) !== true;
    var __expect19 = false;
  }
  