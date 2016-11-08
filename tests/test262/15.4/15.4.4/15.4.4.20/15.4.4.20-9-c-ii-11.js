  function testcase() 
  {
    function callbackfn(val, idx) 
    {
      return val > 10 && arguments[2][idx] === val;
    }
    var newArr = [11, ].filter(callbackfn);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  