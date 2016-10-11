  function testcase() 
  {
  "use strict";
    var _8_7_2_7 = {
      
    };
    var _8_7_2_7_bValue = 1;
    Object.defineProperty(_8_7_2_7, "b", {
      get : (function () 
      {
        return _8_7_2_7_bValue;
      }),
      set : (function (value) 
      {
        _8_7_2_7_bValue = value;
      })
    });
    _8_7_2_7.b = 11;
    return _8_7_2_7.b === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  