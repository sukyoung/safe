  function testcase() 
  {
    var result = false;
    var objError = new RangeError();
    function callbackfn(val, idx, obj) 
    {
      result = (this === objError);
    }
    [11, ].forEach(callbackfn, objError);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  