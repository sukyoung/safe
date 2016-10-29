  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['enum'] = 0;
    tokenCodes['extends'] = 1;
    tokenCodes['super'] = 2;
    var arr = ['enum', 'extends', 'super', ];
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
  