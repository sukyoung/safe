  function testcase() 
  {
    var arr = [];
    Object.defineProperties(arr, {
      "0" : {
        writable : true,
        enumerable : true,
        configurable : false
      }
    });
    return arr.hasOwnProperty("0") && typeof (arr[0]) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  