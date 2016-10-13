  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === Math;
    }
    var newArr = [11, ].filter(callbackfn, Math);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  