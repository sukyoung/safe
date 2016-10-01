  function testcase() 
  {
    var obj = {
      toString : (function () 
      {
        return "abc";
      })
    };
    return (String.prototype.trim.call(obj) === "abc");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  