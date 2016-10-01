  function testcase() 
  {
    var funObj = (function () 
    {
      
    });
    var preCheck = Object.isExtensible(funObj);
    Object.preventExtensions(funObj);
    funObj[0] = 12;
    return preCheck && ! funObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  