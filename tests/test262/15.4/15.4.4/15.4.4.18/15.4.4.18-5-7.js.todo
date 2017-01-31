  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this === eval);
    }
    [11, ].forEach(callbackfn, eval);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  