  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes.public = 0;
    tokenCodes.yield = 1;
    tokenCodes.interface = 2;
    var arr = ['public', 'yield', 'interface', ];
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
  