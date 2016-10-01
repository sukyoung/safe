  function testcase() 
  {
    var arr = [];
    arr.length = 3;
    Object.defineProperties(arr, {
      "1" : {
        value : 26
      }
    });
    return arr.length === 3 && arr[1] === 26;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  