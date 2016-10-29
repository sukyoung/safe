  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['while'] = 0;
    tokenCodes['debugger'] = 1;
    tokenCodes['function'] = 2;
    var arr = ['while', 'debugger', 'function', ];
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
  