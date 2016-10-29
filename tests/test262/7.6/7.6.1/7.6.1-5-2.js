  function testcase() 
  {
    var tokenCodes = {
      break : 0,
      case : 1,
      do : 2
    };
    var arr = ['break', 'case', 'do', ];
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
  