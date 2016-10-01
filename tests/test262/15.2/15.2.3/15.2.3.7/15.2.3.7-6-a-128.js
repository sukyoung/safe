  function testcase() 
  {
    var arr = [];
    Object.defineProperties(arr, {
      length : {
        value : 12
      }
    });
    return arr.length === 12;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  