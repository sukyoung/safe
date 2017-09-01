  function testcase() 
  {
    var func = (function (x, y, z) 
    {
      return x + y + z;
    });
    var newFunc = Function.prototype.bind.call(func, {
      
    }, "a", "b", "c");
    return newFunc() === "abc";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  