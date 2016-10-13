  function testcase() 
  {
    var thisArg = {
      threshold : 10
    };
    function callbackfn(val, idx, obj) 
    {
      return this === thisArg;
    }
    var obj = {
      0 : 11,
      length : 1
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn, thisArg);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  