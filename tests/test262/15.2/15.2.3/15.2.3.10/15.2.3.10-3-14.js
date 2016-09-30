  function testcase() 
  {
    var arrObj = [];
    var preCheck = Object.isExtensible(arrObj);
    Object.preventExtensions(arrObj);
    arrObj.exName = 2;
    return preCheck && ! arrObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  