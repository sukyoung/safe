  var y;
  function f() 
  {
    var x;
    if (x === undefined)
    {
      x = 0;
    }
    else
    {
      x = 1;
    }
    return x;
  }
  y = f();
  y = f();
  {
    var __result1 = ! (y === 0);
    var __expect1 = false;
  }
  