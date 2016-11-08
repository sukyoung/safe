  function testcase() 
  {
    var innerThisCorrect = false;
    function callbackfn(val, idx, obj) 
    {
    "use strict";
      innerThisCorrect = this === undefined;
      return true;
    }
    [1, ].filter(callbackfn);
    return innerThisCorrect;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  