  function testcase() 
  {
    var argObj;
    (function () 
    {
      argObj = arguments;
    })();
    var preCheck = Object.isExtensible(argObj);
    Object.preventExtensions(argObj);
    argObj.exName = 2;
    return preCheck && ! argObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  