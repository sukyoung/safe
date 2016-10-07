  {
    var __result1 = "1" >> 1 !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = 1 >> "1" !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = new String("1") >> 1 !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = 1 >> new String("1") !== 0;
    var __expect4 = false;
  }
  {
    var __result5 = "1" >> new Number(1) !== 0;
    var __expect5 = false;
  }
  {
    var __result6 = new Number(1) >> "1" !== 0;
    var __expect6 = false;
  }
  {
    var __result7 = new String("1") >> new Number(1) !== 0;
    var __expect7 = false;
  }
  {
    var __result8 = new Number(1) >> new String("1") !== 0;
    var __expect8 = false;
  }
  {
    var __result9 = "x" >> 1 !== 0;
    var __expect9 = false;
  }
  {
    var __result10 = 1 >> "x" !== 1;
    var __expect10 = false;
  }
  