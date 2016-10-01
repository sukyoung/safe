  function testcase() 
  {
    var arrObj = [0, 1, ];
    Object.defineProperty(arrObj, "length", {
      value : - 0
    });
    return arrObj.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  