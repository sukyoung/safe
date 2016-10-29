  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes.package = 0;
    tokenCodes.protected = 1;
    tokenCodes.static = 2;
    var arr = ['package', 'protected', 'static', ];
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
  