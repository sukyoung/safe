  function testcase() 
  {
    var accessed = false;
    var objDate = new Date();
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objDate;
    }
    var newArr = [11, ].filter(callbackfn, objDate);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  