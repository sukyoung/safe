  function testcase() 
  {
    try
{      var arr = ["a", "b", "c", ];
      Array.prototype[0] = "test";
      var newArr = arr.splice(2, 1, "d");
      var verifyValue = false;
      verifyValue = arr.length === 3 && arr[0] === "a" && arr[1] === "b" && arr[2] === "d" && newArr[0] === "c" && newArr.length === 1;
      var verifyEnumerable = false;
      for(var p in newArr)
      {
        if (newArr.hasOwnProperty("0") && p === "0")
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
  