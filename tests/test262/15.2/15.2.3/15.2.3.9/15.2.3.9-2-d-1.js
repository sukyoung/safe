  function testcase() 
  {
    var funObj = (function () 
    {
      
    });
    Object.freeze(funObj);
    return Object.isFrozen(funObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  