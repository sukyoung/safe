  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['undefined'] = 0;
    tokenCodes['NaN'] = 1;
    tokenCodes['Infinity'] = 2;
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
  