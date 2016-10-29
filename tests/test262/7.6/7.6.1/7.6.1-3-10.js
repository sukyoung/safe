  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['in'] = 0;
    tokenCodes['try'] = 1;
    tokenCodes['class'] = 2;
    var arr = ['in', 'try', 'class', ];
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
  