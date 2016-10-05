  function testcase() 
  {
    return [true, ].indexOf(true, NaN) === 0 && [true, ].indexOf(true, - NaN) === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  