  x = "1";
  x <<= 1;
  {
    var __result1 = x !== 2;
    var __expect1 = false;
  }
  x = 1;
  x <<= "1";
  {
    var __result2 = x !== 2;
    var __expect2 = false;
  }
  x = new String("1");
  x <<= 1;
  {
    var __result3 = x !== 2;
    var __expect3 = false;
  }
  x = 1;
  x <<= new String("1");
  {
    var __result4 = x !== 2;
    var __expect4 = false;
  }
  x = "1";
  x <<= new Number(1);
  {
    var __result5 = x !== 2;
    var __expect5 = false;
  }
  x = new Number(1);
  x <<= "1";
  {
    var __result6 = x !== 2;
    var __expect6 = false;
  }
  x = new String("1");
  x <<= new Number(1);
  {
    var __result7 = x !== 2;
    var __expect7 = false;
  }
  x = new Number(1);
  x <<= new String("1");
  {
    var __result8 = x !== 2;
    var __expect8 = false;
  }
  x = "x";
  x <<= 1;
  {
    var __result9 = x !== 0;
    var __expect9 = false;
  }
  x = 1;
  x <<= "x";
  {
    var __result10 = x !== 1;
    var __expect10 = false;
  }
  