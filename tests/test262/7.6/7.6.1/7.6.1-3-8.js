  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['this'] = 0;
    tokenCodes['with'] = 1;
    tokenCodes['default'] = 2;
    var arr = ['this', 'with', 'default', ];
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
  