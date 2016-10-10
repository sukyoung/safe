  function testcase() 
  {
    var accessed = false;
    var boolObj = new Boolean(false);
    for(var i = 0;boolObj;)
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
  