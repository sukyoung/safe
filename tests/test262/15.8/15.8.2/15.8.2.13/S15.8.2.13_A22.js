  x = - 0;
  y = new Array();
  y[4] = - 0.000000000000001;
  y[3] = - 2;
  y[2] = - Math.PI;
  y[1] = - 1.7976931348623157e308;
  y[0] = - Infinity;
  ynum = 5;
  for (i = 0;i < ynum;i++)
  {
    {
      var __result1 = Math.pow(x, y[i]) !== + Infinity;
      var __expect1 = false;
    }
  }
  