  function testcase() 
  {
    var strObj = new String("bbq");
    var preCheck = Object.isExtensible(strObj);
    Object.preventExtensions(strObj);
    strObj.exName = 2;
    return preCheck && ! strObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  