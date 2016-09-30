  x = [];
  k = 1;
  for (i = 0;i < 32;i++)
  {
    k = k * 2;
    x[k - 2] = k;
  }
  k = 1;
  for (i = 0;i < 32;i++)
  {
    k = k * 2;
    {
      var __result1 = x[k - 2] !== k;
      var __expect1 = false;
    }
  }
  