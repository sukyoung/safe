  function testcase() 
  {
    var arr = [];
    var isOwnProperty = false;
    var canWritable = false;
    Object.defineProperties(arr, {
      "0" : {
        value : 1001,
        enumerable : true,
        configurable : false
      }
    });
    isOwnProperty = arr.hasOwnProperty("0");
    arr[0] = 12;
    canWritable = (arr[0] === 12);
    return isOwnProperty && ! canWritable && arr[0] === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  