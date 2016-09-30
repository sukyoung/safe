  function testcase() 
  {
    var funObj = (function () 
    {
      
    });
    var preCheck = Object.isExtensible(funObj);
    Object.preventExtensions(funObj);
    funObj.exName = 2;
    return preCheck && ! funObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  