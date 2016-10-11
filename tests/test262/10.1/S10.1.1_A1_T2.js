  var x = (function f1() 
  {
    return 1;
  })();
  if (x !== 1)
    $ERROR('#1: Create function dynamically either by using a FunctionExpression');
  var y = (function () 
  {
    return 2;
  })();
  {
    var __result1 = y !== 2;
    var __expect1 = false;
  }
  var z = (function () 
  {
    return 3;
  })();
  {
    var __result2 = z !== 3;
    var __expect2 = false;
  }
  