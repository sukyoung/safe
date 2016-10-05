  function testcase() 
  {
    var obj = {
      0 : true,
      1 : true
    };
    return Array.prototype.indexOf.call(obj, true) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  