  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10 && obj[idx] === val;
    }
    var newArr = [11, ].filter(callbackfn);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  