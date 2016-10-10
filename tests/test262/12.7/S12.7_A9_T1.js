  var x = 0, y = 0;
  (function () 
  {
    FOR : for (;;)
    {
      try
{        x++;
        if (x === 10)
          return;
        throw 1;
}
      catch (e)
{        continue FOR;}

    }
  })();
  {
    var __result1 = x !== 10;
    var __expect1 = false;
  }

