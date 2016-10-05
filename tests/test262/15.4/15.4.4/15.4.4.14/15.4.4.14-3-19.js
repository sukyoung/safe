  function testcase() 
  {
    var obj = {
      1 : true,
      2 : 2,
      length : {
        toString : (function () 
        {
          return '2';
        })
      }
    };
    return Array.prototype.indexOf.call(obj, true) === 1 && Array.prototype.indexOf.call(obj, 2) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  