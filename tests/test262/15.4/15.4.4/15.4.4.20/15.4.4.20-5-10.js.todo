  function testcase() 
  {
    var accessed = false;
    var objArray = new Array(10);
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objArray;
    }
    var newArr = [11, ].filter(callbackfn, objArray);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  