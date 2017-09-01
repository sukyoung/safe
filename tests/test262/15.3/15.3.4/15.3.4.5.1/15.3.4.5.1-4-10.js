  function testcase() 
  {
    var obj = {
      prop : "abc"
    };
    var func = (function (x) 
    {
      return this === obj && typeof x === "undefined";
    });
    var newFunc = Function.prototype.bind.call(func, obj);
    return newFunc();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  