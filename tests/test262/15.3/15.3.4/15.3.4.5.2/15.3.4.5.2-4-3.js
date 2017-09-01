  function testcase() 
  {
    var func = (function () 
    {
      return new Boolean(arguments.length === 0);
    });
    var NewFunc = Function.prototype.bind.call(func);
    var newInstance = new NewFunc();
    return newInstance.valueOf() === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  