  function testcase() 
  {
    var regObj = new RegExp();
    var preCheck = Object.isExtensible(regObj);
    Object.preventExtensions(regObj);
    regObj.exName = 2;
    return preCheck && ! regObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  