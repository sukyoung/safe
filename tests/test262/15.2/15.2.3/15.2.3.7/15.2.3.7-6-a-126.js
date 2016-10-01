  function testcase() 
  {
    var arr = [0, 1, ];
    Object.defineProperties(arr, {
      length : {
        value : + 0
      }
    });
    return arr.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  