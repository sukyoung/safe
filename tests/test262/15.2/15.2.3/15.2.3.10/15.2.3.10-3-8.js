  function testcase() 
  {
    var dateObj = new Date();
    var preCheck = Object.isExtensible(dateObj);
    Object.preventExtensions(dateObj);
    dateObj[0] = 12;
    return preCheck && ! dateObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  