  y = + 0;
  x = new Array();
  x[0] = - Infinity;
  x[1] = - 1.7976931348623157e308;
  x[2] = - 0.000000000000001;
  x[3] = - 0;
  x[4] = + 0;
  x[5] = 0.000000000000001;
  x[6] = 1.7976931348623157e308;
  x[7] = + Infinity;
  x[8] = NaN;
  xnum = 9;
  for (i = 0;i < xnum;i++)
  {
    {
      var __result1 = Math.pow(x[i], y) !== 1;
      var __expect1 = false;
    }
  }
  