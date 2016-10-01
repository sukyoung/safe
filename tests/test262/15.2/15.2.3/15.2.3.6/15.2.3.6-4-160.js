  function testcase() 
  {
    var arrObj = [0, , 2, ];
    Object.defineProperty(arrObj, "length", {
      value : 3
    });
    return arrObj.length === 3 && arrObj[0] === 0 && ! arrObj.hasOwnProperty("1") && arrObj[2] === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  