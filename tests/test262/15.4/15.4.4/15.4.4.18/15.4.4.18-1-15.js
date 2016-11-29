  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = ('[object Arguments]' === Object.prototype.toString.call(obj));
    }
    var obj = (function () 
    {
      return arguments;
    })("a", "b");
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  