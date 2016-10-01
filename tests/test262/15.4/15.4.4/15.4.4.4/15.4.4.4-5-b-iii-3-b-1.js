  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "0", {
        value : 100,
        writable : false,
        configurable : true
      });
      var oldArr = [101, ];
      var newArr = Array.prototype.concat.call(oldArr);
      var verifyValue = false;
      verifyValue = newArr[0] === 101;
      var verifyEnumerable = false;
      for(var p in newArr)
      {
        if (p === "0" && newArr.hasOwnProperty("0"))
        {
          verifyEnumerable = true;
        }
      }
      var verifyWritable = false;
      newArr[0] = 12;
      verifyWritable = newArr[0] === 12;
      var verifyConfigurable = false;
      delete newArr[0];
      verifyConfigurable = newArr.hasOwnProperty("0");
      return verifyValue && ! verifyConfigurable && verifyEnumerable && verifyWritable;}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  