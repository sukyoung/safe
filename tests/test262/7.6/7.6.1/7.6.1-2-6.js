  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes.continue = 0;
    tokenCodes.for = 1;
    tokenCodes.switch = 2;
    var arr = ['continue', 'for', 'switch', ];
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
  