  function testcase() 
  {
    var tokenCodes = {
      undefined : 0,
      NaN : 1,
      Infinity : 2
    };
    var arr = ['undefined', 'NaN', 'Infinity', ];
    for(var p in tokenCodes)
    {
      for(var p1 in arr)
      {
        if (arr[p1] === p)
        {
          if (! tokenCodes.hasOwnProperty(arr[p1]))
          {
            return false;
          }
          ;
        }
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  