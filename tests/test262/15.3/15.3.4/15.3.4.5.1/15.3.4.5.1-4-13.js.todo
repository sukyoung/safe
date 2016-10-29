  function testcase() 
  {
    var obj = {
      prop : "abc"
    };
    var func = (function (x) 
    {
      return this === obj && x === 1 && arguments[0] === 1 && arguments.length === 1 && this.prop === "abc";
    });
    var newFunc = Function.prototype.bind.call(func, obj, 1);
    return newFunc();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  