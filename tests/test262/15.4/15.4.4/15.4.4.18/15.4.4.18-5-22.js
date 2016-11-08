  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this.valueOf() === false);
    }
    [11, ].forEach(callbackfn, false);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
