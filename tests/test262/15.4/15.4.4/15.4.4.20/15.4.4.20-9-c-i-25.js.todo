  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val === 11 && idx === 0;
    }
    var func = (function (a, b) 
    {
      return Array.prototype.filter.call(arguments, callbackfn);
    });
    var newArr = func(11);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  