  function testcase() 
  {
    var func = (function (x) 
    {
      return new Boolean(arguments.length === 2 && x === 1 && arguments[1] === 2 && arguments[0] === 1);
    });
    var NewFunc = Function.prototype.bind.call(func, {
      
    }, 1, 2);
    var newInstance = new NewFunc();
    return newInstance.valueOf() === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  