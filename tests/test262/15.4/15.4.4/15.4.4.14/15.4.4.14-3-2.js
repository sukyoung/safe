  function testcase() 
  {
    var obj = {
      0 : 0,
      1 : 1,
      length : true
    };
    return Array.prototype.indexOf.call(obj, 0) === 0 && Array.prototype.indexOf.call(obj, 1) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  