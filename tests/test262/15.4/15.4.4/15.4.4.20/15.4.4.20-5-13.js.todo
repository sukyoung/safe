  function testcase() 
  {
    var accessed = false;
    var objNumber = new Number();
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objNumber;
    }
    var newArr = [11, ].filter(callbackfn, objNumber);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  