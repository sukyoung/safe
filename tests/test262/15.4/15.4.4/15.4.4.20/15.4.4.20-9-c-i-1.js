  function testcase() 
  {
    var kValue = {
      
    };
    function callbackfn(val, idx, obj) 
    {
      return (idx === 5) && (val === kValue);
    }
    var obj = {
      5 : kValue,
      length : 100
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 1 && newArr[0] === kValue;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  