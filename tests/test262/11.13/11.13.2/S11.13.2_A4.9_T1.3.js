  x = "1";
  x &= "1";
  {
    var __result1 = x !== 1;
    var __expect1 = false;
  }
  x = new String("1");
  x &= "1";
  {
    var __result2 = x !== 1;
    var __expect2 = false;
  }
  x = "1";
  x &= new String("1");
  {
    var __result3 = x !== 1;
    var __expect3 = false;
  }
  x = new String("1");
  x &= new String("1");
  {
    var __result4 = x !== 1;
    var __expect4 = false;
  }
  x = "x";
  x &= "1";
  {
    var __result5 = x !== 0;
    var __expect5 = false;
  }
  x = "1";
  x &= "x";
  {
    var __result6 = x !== 0;
    var __expect6 = false;
  }
  