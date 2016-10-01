  function testcase() 
  {
    var arr = [];
    var beforeDeleted = false;
    var afterDeleted = false;
    Object.defineProperties(arr, {
      "0" : {
        value : 1001,
        writable : true,
        enumerable : true
      }
    });
    beforeDeleted = arr.hasOwnProperty("0");
    delete arr[0];
    afterDeleted = arr.hasOwnProperty("0");
    return beforeDeleted && afterDeleted && arr[0] === 1001;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  