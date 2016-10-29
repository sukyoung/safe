  function testcase() 
  {
    var tokenCodes = {
      enum : 0,
      extends : 1,
      super : 2
    };
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
  