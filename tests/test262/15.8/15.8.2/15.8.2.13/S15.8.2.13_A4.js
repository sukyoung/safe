  x = NaN;
  y = new Array();
  y[0] = - Infinity;
  y[1] = - 1.7976931348623157e308;
  y[2] = - 0.000000000000001;
  y[3] = 0.000000000000001;
  y[4] = 1.7976931348623157e308;
  y[5] = + Infinity;
  y[6] = NaN;
  ynum = 7;
  for (i = 0;i < ynum;i++)
  {
    {
      var __result1 = ! isNaN(Math.pow(x, y[i]));
      var __expect1 = false;
    }
  }
  