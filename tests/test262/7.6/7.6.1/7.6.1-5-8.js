  function testcase() 
  {
    var tokenCodes = {
      this : 0,
      with : 1,
      default : 2
    };
    var arr = ['this', 'with', 'default', ];
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
  