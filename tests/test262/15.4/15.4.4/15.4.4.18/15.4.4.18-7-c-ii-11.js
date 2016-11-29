  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx) 
    {
      result = (val > 10 && arguments[2][idx] === val);
    }
    [11, ].forEach(callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  