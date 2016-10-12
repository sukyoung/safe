  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['implements'] = 0;
    tokenCodes['let'] = 1;
    tokenCodes['private'] = 2;
    var arr = ['implements', 'let', 'private', ];
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
  