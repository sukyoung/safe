  function testcase() 
  {
    var tokenCodes = {
      if : 0,
      throw : 1,
      delete : 2
    };
    var arr = ['if', 'throw', 'delete', ];
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
  