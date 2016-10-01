  for (i = 0;i <= 1000;i++)
  {
    x = i / 10.0;
    {
      var __result1 = Math.round(x) !== Math.floor(x + 0.5);
      var __expect1 = false;
    }
  }
  for (i = - 5;i >= - 1000;i--)
  {
    if (i === - 5)
    {
      x = - 0.500000000000001;
    }
    else
    {
      x = i / 10.0;
    }
    {
      var __result2 = Math.round(x) !== Math.floor(x + 0.5);
      var __expect2 = false;
    }
  }
  