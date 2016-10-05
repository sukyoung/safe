  function testcase() 
  {
    var accessed = false;
    var obj = {
      value : false
    };
    for(var i = 0;obj;)
    {
      accessed = true;
      break;
    }
    return accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  