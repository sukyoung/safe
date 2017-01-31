  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val;
    }
    var obj = {
      0 : 11,
      1 : 9,
      length : 2
    };
    var newArr = Array.prototype.map.call(obj, callbackfn);
    return newArr[0] === obj[0] && newArr[1] === obj[1];
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  