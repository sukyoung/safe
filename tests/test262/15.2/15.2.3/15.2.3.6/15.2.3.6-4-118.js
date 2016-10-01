  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "length", {
      
    });
    var verifyValue = false;
    if (arrObj.length === 0)
    {
      verifyValue = true;
    }
    arrObj.length = 2;
    var verifyWritable = arrObj.length === 2;
    var verifyEnumerable = false;
    for(var p in arrObj)
    {
      if (p === "length" && arrObj.hasOwnProperty(p))
      {
        verifyEnumerable = true;
      }
    }
    delete arrObj.length;
    var verifyConfigurable = arrObj.hasOwnProperty("length");
    return verifyValue && verifyWritable && ! verifyEnumerable && verifyConfigurable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  