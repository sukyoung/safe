  function testcase() 
  {
    var accessed = false;
    var objError = new RangeError();
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objError;
    }
    var newArr = [11, ].filter(callbackfn, objError);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  