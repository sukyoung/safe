  function testcase() 
  {
    var obj = {
      prop : "abc"
    };
    var func = (function () 
    {
      return this === obj && arguments[0] === 1;
    });
    var newFunc = Function.prototype.bind.call(func, obj, 1);
    return newFunc();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  