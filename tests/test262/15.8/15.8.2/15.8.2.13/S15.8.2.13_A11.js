  x = + Infinity;
  y = new Array();
  y[3] = Infinity;
  y[2] = 1.7976931348623157e308;
  y[1] = 1;
  y[0] = 0.000000000000001;
  ynum = 4;
  for (i = 0;i < ynum;i++)
  {
    {
      var __result1 = Math.pow(x, y[i]) !== + Infinity;
      var __expect1 = false;
    }
  }
  