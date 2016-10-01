  function testcase() 
  {
    var arrObj = [];
    Object.defineProperty(arrObj, "length", {
      writable : true,
      enumerable : false,
      configurable : false
    });
    var verifyValue = false;
    if (arrObj.length === 0)
    {
      verifyValue = true;
    }
    arrObj.length = 2;
    var verifyWritable = arrObj.length === 2 ? true : false;
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
  