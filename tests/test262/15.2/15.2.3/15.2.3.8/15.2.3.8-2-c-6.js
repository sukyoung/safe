  function testcase() 
  {
    var dateObj = new Date();
    var preCheck = Object.isExtensible(dateObj);
    Object.seal(dateObj);
    return preCheck && Object.isSealed(dateObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  