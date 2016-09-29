  {
    var __result1 = ! isNaN(Math.max(NaN));
    var __expect1 = false;
  }
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
  for (i = 0;i <= 1;i++)
  {
    args[i] = NaN;
    for (j = 0;j < valnum;j++)
    {
      args[1 - i] = vals[j];
      {
        var __result2 = ! isNaN(Math.max(args[0], args[1]));
        var __expect2 = false;
      }
    }
  }
  var k = 1;
  var l = 2;
  for (i = 0;i <= 2;i++)
  {
    args[i] = NaN;
    if (i === 1)
    {
      k = 0;
    }
    else
      if (i === 2)
      {
        l = 1;
      }
    for (j = 0;j < valnum;j++)
    {
      for (jj = 0;jj < valnum;jj++)
      {
        args[k] = vals[j];
        args[l] = vals[jj];
        {
          var __result3 = ! isNaN(Math.max(args[0], args[1], args[2]));
          var __expect3 = false;
        }
      }
    }
  }
  