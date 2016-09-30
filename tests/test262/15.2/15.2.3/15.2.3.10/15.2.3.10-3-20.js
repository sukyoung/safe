  function testcase() 
  {
    var errObj = new Error();
    var preCheck = Object.isExtensible(errObj);
    Object.preventExtensions(errObj);
    errObj.exName = 2;
    return preCheck && ! errObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  