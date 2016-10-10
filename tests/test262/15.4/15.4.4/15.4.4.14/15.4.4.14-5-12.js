  function testcase() 
  {
    var arr = [];
    arr[Math.pow(2, 32) - 2] = true;
    return arr.indexOf(true, Infinity) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  