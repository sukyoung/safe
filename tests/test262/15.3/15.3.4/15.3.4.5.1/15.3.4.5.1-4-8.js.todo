  function testcase() 
  {
    var obj = {
      prop : "abc"
    };
    var func = (function () 
    {
      return this === obj && arguments[0] === 1;
    });
    var newFunc = Function.prototype.bind.call(func, obj);
    return newFunc(1);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  