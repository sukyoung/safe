  function testcase() 
  {
    var func = (function () 
    {
      return arguments[0] === 1;
    });
    var newFunc = Function.prototype.bind.call(func);
    return newFunc(1);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  