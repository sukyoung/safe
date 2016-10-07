  x = 1;
  x <<= 1;
  {
    var __result1 = x !== 2;
    var __expect1 = false;
  }
  x = new Number(1);
  x <<= 1;
  {
    var __result2 = x !== 2;
    var __expect2 = false;
  }
  x = 1;
  x <<= new Number(1);
  {
    var __result3 = x !== 2;
    var __expect3 = false;
  }
  x = new Number(1);
  x <<= new Number(1);
  {
    var __result4 = x !== 2;
    var __expect4 = false;
  }
  