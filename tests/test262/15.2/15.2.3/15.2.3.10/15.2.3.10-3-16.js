  function testcase() 
  {
    var boolObj = new Boolean(true);
    var preCheck = Object.isExtensible(boolObj);
    Object.preventExtensions(boolObj);
    boolObj.exName = 2;
    return preCheck && ! boolObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  