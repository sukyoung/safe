  function testcase() 
  {
    var result = false;
    var objRegExp = new RegExp();
    function callbackfn(val, idx, obj) 
    {
      result = (this === objRegExp);
    }
    [11, ].forEach(callbackfn, objRegExp);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  