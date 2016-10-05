  function testcase() 
  {
    var targetObj = [];
    var obj = {
      0 : targetObj,
      100 : targetObj,
      length : 0.1
    };
    return Array.prototype.indexOf.call(obj, targetObj) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  