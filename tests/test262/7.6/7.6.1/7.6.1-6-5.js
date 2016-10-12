  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes.finally = 0;
    tokenCodes.return = 1;
    tokenCodes.void = 2;
    var arr = ['finally', 'return', 'void', ];
    for(var i = 0;i < arr.length;i++)
    {
      if (tokenCodes[arr[i]] !== i)
      {
        return false;
      }
      ;
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  