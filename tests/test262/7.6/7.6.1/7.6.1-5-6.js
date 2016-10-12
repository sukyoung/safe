  function testcase() 
  {
    var tokenCodes = {
      continue : 0,
      for : 1,
      switch : 2
    };
    var arr = ['continue', 'for', 'switch', ];
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
  