  y = + Infinity;
  x = new Array();
  x[0] = - Infinity;
  x[1] = - 1.7976931348623157e308;
  x[2] = - 1.000000000000001;
  x[3] = 1.000000000000001;
  x[4] = 1.7976931348623157e308;
  x[5] = + Infinity;
  xnum = 6;
  for (i = 0;i < xnum;i++)
  {
    {
      var __result1 = Math.pow(x[i], y) !== + Infinity;
      var __expect1 = false;
    }
  }
  