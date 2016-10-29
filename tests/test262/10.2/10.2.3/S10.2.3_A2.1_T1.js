  for(var x in this)
  {
    if (x === 'NaN')
    {
      $ERROR("#1: 'NaN' have attribute DontEnum");
    }
    else
      if (x === 'Infinity')
      {
        $ERROR("#1: 'Infinity' have attribute DontEnum");
      }
      else
      {
        var __result1 = x === 'undefined';
        var __expect1 = false;
      }
  }
  