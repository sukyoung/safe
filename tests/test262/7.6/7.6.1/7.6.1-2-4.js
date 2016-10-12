  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes.new = 0;
    tokenCodes.var = 1;
    tokenCodes.catch = 2;
    var arr = ['new', 'var', 'catch', ];
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
  