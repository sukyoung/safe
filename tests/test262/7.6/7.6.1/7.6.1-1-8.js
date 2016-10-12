  function testcase() 
  {
    var tokenCodes = {
      this : 0,
      with : 1,
      default : 2
    };
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
  