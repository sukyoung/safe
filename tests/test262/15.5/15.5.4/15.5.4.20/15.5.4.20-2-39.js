  function testcase() 
  {
    var obj = {
      valueOf : (function () 
      {
        return "abc";
      })
    };
    return (String.prototype.trim.call(obj) === "[object Object]");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  