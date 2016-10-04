  function testcase() 
  {
    var accessed = false;
    var numObj = new Number(12);
    for(var i = 0;numObj;)
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
  