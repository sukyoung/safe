  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "0", {
      value : 12
    });
    return arrObj[0] === 12;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  