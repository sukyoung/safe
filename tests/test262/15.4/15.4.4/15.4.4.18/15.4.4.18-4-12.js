  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
    }
    [11, 9, ].forEach(callbackfn);
    return accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  