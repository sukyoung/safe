  function testcase() 
  {
    var boolObj = new Boolean(false);
    boolObj.foo = 10;
    var preCheck = Object.isExtensible(boolObj);
    Object.seal(boolObj);
    delete boolObj.foo;
    return preCheck && boolObj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  