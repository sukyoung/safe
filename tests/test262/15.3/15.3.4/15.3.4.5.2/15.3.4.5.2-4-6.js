  function testcase() 
  {
    var func = (function () 
    {
      return new Boolean(arguments.length === 1 && arguments[0] === 1);
    });
    var NewFunc = Function.prototype.bind.call(func, {
      
    }, 1);
    var newInstance = new NewFunc();
    return newInstance.valueOf() === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  