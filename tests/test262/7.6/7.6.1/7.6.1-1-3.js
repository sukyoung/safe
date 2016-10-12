  function testcase() 
  {
    var tokenCodes = {
      instanceof : 0,
      typeof : 1,
      else : 2
    };
    var arr = ['instanceof', 'typeof', 'else', ];
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
  