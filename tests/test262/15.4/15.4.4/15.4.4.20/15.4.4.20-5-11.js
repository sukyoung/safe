  function testcase() 
  {
    var accessed = false;
    var objString = new String();
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objString;
    }
    var newArr = [11, ].filter(callbackfn, objString);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  