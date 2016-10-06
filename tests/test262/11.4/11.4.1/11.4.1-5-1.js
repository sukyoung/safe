  function testcase() 
  {
    var x = 1;
    var d = delete x;
    if (d === false && x === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  