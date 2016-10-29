  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['enum'] = 0;
    tokenCodes['extends'] = 1;
    tokenCodes['super'] = 2;
    var arr = ['enum', 'extends', 'super', ];
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
  