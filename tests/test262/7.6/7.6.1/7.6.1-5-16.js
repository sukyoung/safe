  function testcase() 
  {
    var tokenCodes = {
      undefined : 0,
      NaN : 1,
      Infinity : 2
    };
    var arr = ['undefined', 'NaN', 'Infinity', ];
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
  