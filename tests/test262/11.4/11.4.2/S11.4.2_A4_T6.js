  var x = 0;
  if (void (x = 1) !== undefined)
  {
    $ERROR('#1: var x = 0; void (x = 1) === undefined. Actual: ' + (void (x = 1)));
  }
  else
  {
    {
      var __result1 = x !== 1;
      var __expect1 = false;
    }
  }
  