  function testcase() 
  {
    var arr = [0, ];
    Object.defineProperties(arr, {
      "0" : {
        value : 12
      }
    });
    return arr[0] === 12;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  