  function testcase() 
  {
    var called = 0;
    function callbackfn(val, idx, obj) 
    {
      called++;
      return val > 10;
    }
    var obj = {
      0 : 11,
      1 : 8,
      length : 20
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 1 && newArr[0] !== 8 && called === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  