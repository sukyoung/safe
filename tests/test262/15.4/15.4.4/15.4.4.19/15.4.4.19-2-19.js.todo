  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10;
    }
    var fun = (function (a, b) 
    {
      return a + b;
    });
    fun[0] = 12;
    fun[1] = 11;
    fun[2] = 9;
    var testResult = Array.prototype.map.call(fun, callbackfn);
    return 2 === testResult.length;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  