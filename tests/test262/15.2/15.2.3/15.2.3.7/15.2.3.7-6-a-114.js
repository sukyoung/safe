  function testcase() 
  {
    var arr = [];
    Object.defineProperties(arr, {
      length : {
        
      }
    });
    var verifyValue = false;
    verifyValue = (arr.length === 0);
    var verifyWritable = false;
    arr.length = 2;
    verifyWritable = (arr.length === 2);
    var verifyEnumerable = false;
    for(var p in arr)
    {
      if (p === "length")
      {
        verifyEnumerable = true;
      }
    }
    var verifyConfigurable = false;
    delete arr.length;
    verifyConfigurable = arr.hasOwnProperty("length");
    return verifyValue && verifyWritable && ! verifyEnumerable && verifyConfigurable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  