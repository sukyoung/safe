  {
    var __result1 = typeof sinx !== 'undefined';
    var __expect1 = false;
  }
  var __val = (function derivative(f, dx) 
  {
    return (function (x) 
    {
      return (f(x + dx) - f(x)) / dx;
    });
  })((function sinx(x) 
  {
    return Math.sin(x);
  }), 
  .0001)(0.5);

  {
    var __result2 = typeof sinx !== 'undefined';
    var __expect2 = false;
  }
  
