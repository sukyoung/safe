  function testcase() 
  {
    var errObj = new Error();
    var preCheck = Object.isExtensible(errObj);
    Object.preventExtensions(errObj);
    errObj[0] = 12;
    return preCheck && ! errObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  