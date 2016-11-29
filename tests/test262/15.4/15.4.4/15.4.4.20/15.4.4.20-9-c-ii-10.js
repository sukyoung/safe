  function testcase() 
  {
    function callbackfn(val) 
    {
      return val > 10;
    }
    var newArr = [12, ].filter(callbackfn);
    return newArr.length === 1 && newArr[0] === 12;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  