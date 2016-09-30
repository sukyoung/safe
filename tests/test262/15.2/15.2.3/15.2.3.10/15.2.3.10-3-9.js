  function testcase() 
  {
    var regObj = new RegExp();
    var preCheck = Object.isExtensible(regObj);
    Object.preventExtensions(regObj);
    regObj[0] = 12;
    return preCheck && ! regObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  