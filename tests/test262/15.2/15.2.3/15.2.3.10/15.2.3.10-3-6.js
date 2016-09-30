  function testcase() 
  {
    var boolObj = new Boolean(true);
    var preCheck = Object.isExtensible(boolObj);
    Object.preventExtensions(boolObj);
    boolObj[0] = 12;
    return preCheck && ! boolObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  