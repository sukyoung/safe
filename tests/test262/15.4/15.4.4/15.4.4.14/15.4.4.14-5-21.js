  function testcase() 
  {
    var fromIndex = {
      toString : (function () 
      {
        return '1';
      })
    };
    return [0, true, ].indexOf(true, fromIndex) === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  