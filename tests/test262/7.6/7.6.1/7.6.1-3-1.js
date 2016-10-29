  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['null'] = 0;
    tokenCodes['true'] = 1;
    tokenCodes['false'] = 2;
    var arr = ['null', 'true', 'false', ];
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
  