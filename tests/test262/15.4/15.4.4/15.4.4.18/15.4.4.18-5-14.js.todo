  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this === Math);
    }
    [11, ].forEach(callbackfn, Math);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  