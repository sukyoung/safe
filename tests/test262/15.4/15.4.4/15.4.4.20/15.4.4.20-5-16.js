  function testcase() 
  {
    var accessed = false;
    var objRegExp = new RegExp();
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objRegExp;
    }
    var newArr = [11, ].filter(callbackfn, objRegExp);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  