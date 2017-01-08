  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
    }
    var result = [].forEach(callbackfn);
    return typeof result === "undefined" && ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  