  function testcase() 
  {
    var func = (function () 
    {
      return new Boolean(arguments[0] === 1 && arguments.length === 1);
    });
    var NewFunc = Function.prototype.bind.call(func);
    var newInstance = new NewFunc(1);
    return newInstance.valueOf() === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  