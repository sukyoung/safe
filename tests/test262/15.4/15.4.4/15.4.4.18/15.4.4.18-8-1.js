  function testcase() 
  {
    var callCnt = 0;
    function cb() 
    {
      callCnt++;
    }
    var i = [].forEach(cb);
    if (callCnt === 0)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  