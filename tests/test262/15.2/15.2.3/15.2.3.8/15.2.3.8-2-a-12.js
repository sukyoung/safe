  function testcase() 
  {
    var dateObj = new Date();
    dateObj.foo = 10;
    var preCheck = Object.isExtensible(dateObj);
    Object.seal(dateObj);
    delete dateObj.foo;
    return preCheck && dateObj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  