  function testcase() 
  {
    var accessed = false;
    var objFunction = (function () 
    {
      
    });
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return this === objFunction;
    }
    var newArr = [11, ].filter(callbackfn, objFunction);
    return newArr[0] === 11 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  