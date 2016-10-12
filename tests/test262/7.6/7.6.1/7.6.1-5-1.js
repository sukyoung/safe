  function testcase() 
  {
    var tokenCodes = {
      null : 0,
      true : 1,
      false : 2
    };
    var arr = ['null', 'true', 'false', ];
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
  