  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this.valueOf() === 101);
    }
    [11, ].forEach(callbackfn, 101);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  