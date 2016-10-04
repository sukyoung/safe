  function testcase() 
  {
    var accessed = false;
    for(var i = 0;2;)
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
  