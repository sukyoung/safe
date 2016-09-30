  function testcase() 
  {
    var dateObj = new Date();
    var preCheck = Object.isExtensible(dateObj);
    Object.preventExtensions(dateObj);
    dateObj.exName = 2;
    return preCheck && ! dateObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  