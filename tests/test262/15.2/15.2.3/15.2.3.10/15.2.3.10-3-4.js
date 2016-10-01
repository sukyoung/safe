  function testcase() 
  {
    var arrObj = [];
    var preCheck = Object.isExtensible(arrObj);
    Object.preventExtensions(arrObj);
    arrObj[0] = 12;
    return preCheck && ! arrObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  