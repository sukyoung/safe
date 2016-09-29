  vals = new Array();
  vals[0] = - Infinity;
  vals[1] = - 0.000000000000001;
  vals[2] = - 0;
  vals[3] = + 0;
  vals[4] = 0.000000000000001;
  vals[5] = + Infinity;
  vals[6] = NaN;
  valnum = 7;
  args = new Array();
  for (i = 0;i < 2;i++)
  {
    args[i] = NaN;
    for (j = 0;j < valnum;j++)
    {
      args[1 - i] = vals[j];
      {
        var __result1 = ! isNaN(Math.atan2(args[0], args[1]));
        var __expect1 = false;
      }
    }
  }
  