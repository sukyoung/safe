  function testcase() 
  {
    var fromIndex = {
      valueOf : (function () 
      {
        return 1;
      })
    };
    return [0, true, ].indexOf(true, fromIndex) === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  