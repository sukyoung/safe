  function testcase() 
  {
    var tokenCodes = {
      package : 0,
      protected : 1,
      static : 2
    };
    var arr = ['package', 'protected', 'static', ];
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
  