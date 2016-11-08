  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 2;
    }
    var newArr = [12, 11, ].filter(callbackfn);
    return newArr.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  