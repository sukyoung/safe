  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 2;
    }
    var obj = {
      0 : 12,
      1 : 11,
      2 : 9,
      length : 2
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  