  function testcase() 
  {
    var result = false;
    var objBoolean = new Boolean();
    function callbackfn(val, idx, obj) 
    {
      result = (this === objBoolean);
    }
    [11, ].forEach(callbackfn, objBoolean);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  