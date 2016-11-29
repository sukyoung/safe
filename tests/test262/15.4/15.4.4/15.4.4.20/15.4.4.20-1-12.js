  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof RegExp;
    }
    var obj = new RegExp();
    obj.length = 2;
    obj[1] = true;
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  