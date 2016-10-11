  var y;
  function f(a) 
  {
    var x;
    if (a === 1)
      return x;
    else
    {
      if (x === undefined)
      {
        x = 0;
      }
      else
      {
        x = 1;
      }
      return f(1);
    }
  }
  y = f(0);
  {
    var __result1 = ! (y === undefined);
    var __expect1 = false;
  }
  