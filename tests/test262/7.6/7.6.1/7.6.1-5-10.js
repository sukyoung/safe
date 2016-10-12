  function testcase() 
  {
    var tokenCodes = {
      in : 0,
      try : 1,
      class : 2
    };
    var arr = ['in', 'try', 'class', ];
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
  