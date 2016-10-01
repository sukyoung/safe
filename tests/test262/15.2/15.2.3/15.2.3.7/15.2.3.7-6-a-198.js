  function testcase() 
  {
    var arr = [];
    var isOwnProperty = false;
    var canEnumerable = false;
    Object.defineProperties(arr, {
      "0" : {
        value : 1001,
        writable : true,
        configurable : true
      }
    });
    isOwnProperty = arr.hasOwnProperty("0");
    for(var i in arr)
    {
      if (i === "0")
      {
        canEnumerable = true;
      }
    }
    return isOwnProperty && ! canEnumerable && arr[0] === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  