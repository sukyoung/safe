  function testcase() 
  {
    var sum = 0;
    for(var i = 1;i <= 10;i++)
    {
      continue;
      ;
      sum += i;
    }
    return sum === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  