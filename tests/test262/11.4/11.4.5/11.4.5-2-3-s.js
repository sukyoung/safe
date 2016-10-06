  function testcase() 
  {
  "use strict";
    arguments[1] = 7;
    -- arguments[1];
    return arguments[1] === 6;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  