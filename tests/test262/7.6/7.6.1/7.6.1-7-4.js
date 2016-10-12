  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes['new'] = 0;
    tokenCodes['var'] = 1;
    tokenCodes['catch'] = 2;
    var arr = ['new', 'var', 'catch', ];
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
  