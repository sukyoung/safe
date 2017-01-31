  function testcase() 
  {
    var result = false;
    var objNumber = new Number();
    function callbackfn(val, idx, obj) 
    {
      result = (this === objNumber);
    }
    [11, ].forEach(callbackfn, objNumber);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  