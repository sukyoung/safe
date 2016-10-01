  function testcase() 
  {
    var arrObj = [0, 1, 2, 3, ];
    Object.defineProperty(arrObj, "1", {
      configurable : false
    });
    Object.defineProperty(arrObj, "length", {
      value : 3
    });
    return arrObj.length === 3;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  