  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "0", {
      value : NaN
    });
    Object.defineProperty(arrObj, "0", {
      value : NaN
    });
    var hasProperty = arrObj.hasOwnProperty("0");
    var verifyValue = (arrObj[0] !== arrObj[0]);
    var verifyWritable = false;
    arrObj[0] = 1001;
    verifyWritable = arrObj[0] !== 1001 && arrObj[0] !== arrObj[0];
    var verifyEnumerable = false;
    for(var p in arrObj)
    {
      if (p === "0")
      {
        verifyEnumerable = true;
      }
    }
    var verifyConfigurable = false;
    delete arrObj[0];
    verifyConfigurable = arrObj.hasOwnProperty("0");
    return hasProperty && verifyValue && verifyWritable && ! verifyEnumerable && verifyConfigurable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  