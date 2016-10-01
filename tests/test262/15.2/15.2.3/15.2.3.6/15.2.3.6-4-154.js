  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "length", {
      value : 4294967294
    });
    return arrObj.length === 4294967294;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  