  function testcase() 
  {
    var tokenCodes = {
      while : 0,
      debugger : 1,
      function : 2
    };
    var arr = ['while', 'debugger', 'function', ];
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
  