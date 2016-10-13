  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = obj instanceof String;
    }
    Array.prototype.forEach.call("abc", callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  