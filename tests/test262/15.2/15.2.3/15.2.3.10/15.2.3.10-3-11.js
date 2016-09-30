  function testcase() 
  {
    var argObj;
    (function () 
    {
      argObj = arguments;
    })();
    var preCheck = Object.isExtensible(argObj);
    Object.preventExtensions(argObj);
    argObj[0] = 12;
    return preCheck && ! argObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  