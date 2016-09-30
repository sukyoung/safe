  function testcase() 
  {
    var strObj = new String("bbq");
    var preCheck = Object.isExtensible(strObj);
    Object.preventExtensions(strObj);
    strObj[10] = 12;
    return preCheck && ! strObj.hasOwnProperty("10");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  