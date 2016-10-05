  function testcase() 
  {
    var obj = {
      0 : 0,
      length : NaN
    };
    return Array.prototype.indexOf.call(obj, 0) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  