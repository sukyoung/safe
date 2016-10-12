  function testcase() 
  {
    var tokenCodes = {
      break : 0,
      case : 1,
      do : 2
    };
    var arr = ['break', 'case', 'do', ];
    for(var p in tokenCodes)
    {
      for(var p1 in arr)
      {
        if (arr[p1] === p)
        {
          if (! tokenCodes.hasOwnProperty(arr[p1]))
          {
            return false;
          }
          ;
        }
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  