  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (obj.length === 2);
    }
    var fun = (function (a, b) 
    {
      return a + b;
    });
    fun[0] = 12;
    fun[1] = 11;
    fun[2] = 9;
    Array.prototype.forEach.call(fun, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  