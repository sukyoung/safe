  function testcase() 
  {
    var accessed = false;
    var strObj = new String("1");
    for(var i = 0;strObj;)
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
  