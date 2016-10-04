  function DD_operator(f, delta) 
  {
    return (function (x) 
    {
      return (f(x + delta) - 2 * f(x) + f(x - delta)) / (delta * delta);
    });
  }
  DDsin = DD_operator(Math.sin, 0.00001);
  {
    var __result1 = DDsin(Math.PI / 2) + Math.sin(Math.PI / 2) > 0.00001;
    var __expect1 = false;
  }
  