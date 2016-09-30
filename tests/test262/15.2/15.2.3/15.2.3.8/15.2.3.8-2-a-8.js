  function testcase() 
  {
    var arrObj = [];
    arrObj.foo = 10;
    var preCheck = Object.isExtensible(arrObj);
    Object.seal(arrObj);
    delete arrObj.foo;
    return preCheck && arrObj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  