  function testcase() 
  {
    var result = false;
    var arg;
    function callbackfn(val, idx, obj) 
    {
      result = (this === arg);
    }
    (function fun() 
    {
      arg = arguments;
    })(1, 2, 3);
    [11, ].forEach(callbackfn, arg);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  