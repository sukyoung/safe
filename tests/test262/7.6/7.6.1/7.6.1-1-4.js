  function testcase() 
  {
    var tokenCodes = {
      new : 0,
      var : 1,
      catch : 2
    };
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
  