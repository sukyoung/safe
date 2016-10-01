  function testcase() 
  {
    var arr = [];
    Object.defineProperties(arr, {
      "4294967296" : {
        value : 100
      }
    });
    return arr.hasOwnProperty("4294967296") && arr.length === 0 && arr[4294967296] === 100;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  