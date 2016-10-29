  var x = 1;
  var y = 1;
  x;
  -- y;
  if (x !== 1)
  {
    $ERROR('#1: Check Prefix Decrement Operator for automatic semicolon insertion');
  }
  else
  {
    {
      var __result1 = y !== 0;
      var __expect1 = false;
    }
  }
  