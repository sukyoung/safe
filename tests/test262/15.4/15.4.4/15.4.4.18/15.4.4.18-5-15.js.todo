  function testcase() 
  {
    var result = false;
    var objDate = new Date();
    function callbackfn(val, idx, obj) 
    {
      result = (this === objDate);
    }
    [11, ].forEach(callbackfn, objDate);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  