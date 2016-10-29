  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['implements'] = 0;
    tokenCodes['let'] = 1;
    tokenCodes['private'] = 2;
    var arr = ['implements', 'let', 'private', ];
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
  