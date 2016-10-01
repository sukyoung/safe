  function testcase() 
  {
    var arrObj = [];
    arrObj.length = 3;
    Object.defineProperty(arrObj, "1", {
      value : 14
    });
    return arrObj.length === 3 && arrObj[1] === 14;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  