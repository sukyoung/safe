  function testcase() 
  {
    var tokenCodes = {
      new : 0,
      var : 1,
      catch : 2
    };
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
  