  function testcase() 
  {
    var obj = {
      0 : 0,
      1 : 1,
      2 : 2,
      length : 3
    };
    return Array.prototype.indexOf.call(obj, 0) === 0 && Array.prototype.indexOf.call(obj, 1) === 1 && Array.prototype.indexOf.call(obj, 2) === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  