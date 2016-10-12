  function testcase() 
  {
    var tokenCodes = {
      finally : 0,
      return : 1,
      void : 2
    };
    var arr = ['finally', 'return', 'void', ];
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
  