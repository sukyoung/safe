  function testcase() 
  {
    var arr = [];
    Object.defineProperties(arr, {
      length : {
        value : 4294967294
      }
    });
    return arr.length === 4294967294;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  