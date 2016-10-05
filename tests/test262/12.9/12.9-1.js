  function testcase() 
  {
    var sum = 0;
    (function innerTest() 
    {
      for(var i = 1;i <= 10;i++)
      {
        if (i === 6)
        {
          return;
          ;
        }
        sum += i;
      }
    })();
    return sum === 15;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  