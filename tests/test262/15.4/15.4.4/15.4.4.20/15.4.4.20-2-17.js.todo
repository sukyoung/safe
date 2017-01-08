  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 2;
    }
    var func = (function (a, b) 
    {
      var newArr = Array.prototype.filter.call(arguments, callbackfn);
      return newArr.length === 2;
    });
    return func(12, 11);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  