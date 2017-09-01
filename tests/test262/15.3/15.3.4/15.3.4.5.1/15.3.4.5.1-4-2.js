  function testcase() 
  {
    var obj = {
      "prop" : "a"
    };
    var func = (function () 
    {
      return this;
    });
    var newFunc = Function.prototype.bind.call(func, obj);
    return newFunc() === obj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  