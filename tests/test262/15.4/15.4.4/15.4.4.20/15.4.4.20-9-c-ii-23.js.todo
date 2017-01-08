  function testcase() 
  {
    var obj = {
      0 : 11,
      length : 2
    };
    function callbackfn(val, idx, o) 
    {
      return obj === o;
    }
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  