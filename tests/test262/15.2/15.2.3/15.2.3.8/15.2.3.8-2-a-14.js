  function testcase() 
  {
    var errObj = new Error();
    errObj.foo = 10;
    var preCheck = Object.isExtensible(errObj);
    Object.seal(errObj);
    delete errObj.foo;
    return preCheck && errObj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  