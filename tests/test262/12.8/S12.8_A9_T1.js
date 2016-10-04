  var x = 0, y = 0;
  (function () 
  {
    FOR : for (;;)
    {
      try
{        x++;
        if (x === 10)
          return;
        throw 1;}
      catch (e)
{        break FOR;}

    }
  })();
  {
    var __result1 = x !== 1;
    var __expect1 = false;
  }
  