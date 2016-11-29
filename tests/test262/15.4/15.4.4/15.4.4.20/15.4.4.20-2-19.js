  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 2;
    }
    var fun = (function (a, b) 
    {
      return a + b;
    });
    fun[0] = 12;
    fun[1] = 11;
    fun[2] = 9;
    var newArr = Array.prototype.filter.call(fun, callbackfn);
    return newArr.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  