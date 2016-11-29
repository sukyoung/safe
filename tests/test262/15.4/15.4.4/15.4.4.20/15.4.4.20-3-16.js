  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return true;
    }
    var obj = {
      1 : 11,
      2 : 9,
      length : "0x0002"
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  