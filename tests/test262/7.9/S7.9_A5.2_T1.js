  var x = 0;
  var y = 0;
  x;
  ++ y;
  if (x !== 0)
  {
    $ERROR('#1: Check Prefix Increment Operator for automatic semicolon insertion');
  }
  else
  {
    {
      var __result1 = y !== 1;
      var __expect1 = false;
    }
  }
  