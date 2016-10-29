  function testcase() 
  {
    var func = (function (x, y, z) 
    {
      return z;
    });
    var newFunc = Function.prototype.bind.call(func, {
      
    }, "a", "b");
    return newFunc("c") === "c";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  