  x = - 0;
  y = new Array();
  y[0] = 0.000000000000001;
  y[1] = 2;
  y[2] = Math.PI;
  y[3] = 1.7976931348623157e308;
  y[4] = + Infinity;
  ynum = 5;
  for (i = 0;i < ynum;i++)
  {
    {
      var __result1 = Math.pow(x, y[i]) !== + 0;
      var __expect1 = false;
    }
  }
  