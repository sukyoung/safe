  function testcase() 
  {
    var kValue = {
      
    };
    function callbackfn(val, idx, obj) 
    {
      if (idx === 5)
      {
        return val === kValue;
      }
      return false;
    }
    var obj = {
      5 : kValue,
      length : 100
    };
    var newArr = Array.prototype.map.call(obj, callbackfn);
    return newArr[5] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  