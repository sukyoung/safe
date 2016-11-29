  function testcase() 
  {
    var accessed = false;
    var objBoolean = new Boolean();
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objBoolean;
    }
    var newArr = [11, ].filter(callbackfn, objBoolean);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  