  function testcase() 
  {
    var fun = (function () 
    {
      
    });
    var preCheck = Object.isExtensible(fun);
    Object.seal(fun);
    return preCheck && Object.isSealed(fun);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  