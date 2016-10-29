  function testcase() 
  {
    var tokenCodes = {
      public : 0,
      yield : 1,
      interface : 2
    };
    var arr = ['public', 'yield', 'interface', ];
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
  