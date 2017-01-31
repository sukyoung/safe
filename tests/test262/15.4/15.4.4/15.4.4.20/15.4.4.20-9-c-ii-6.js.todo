  function testcase() 
  {
    var obj = {
      0 : 11,
      length : 1
    };
    var thisArg = {
      
    };
    function callbackfn() 
    {
      return this === thisArg && arguments[0] === 11 && arguments[1] === 0 && arguments[2] === obj;
    }
    var newArr = Array.prototype.filter.call(obj, callbackfn, thisArg);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  