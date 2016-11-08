  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this.valueOf() === "abc");
    }
    [11, ].forEach(callbackfn, "abc");
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  