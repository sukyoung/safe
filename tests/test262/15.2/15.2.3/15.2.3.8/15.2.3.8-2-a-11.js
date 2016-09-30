  function testcase() 
  {
    var numObj = new Number(- 1);
    numObj.foo = 10;
    var preCheck = Object.isExtensible(numObj);
    Object.seal(numObj);
    delete numObj.foo;
    return preCheck && numObj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  