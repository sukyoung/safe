  function testcase() 
  {
    var func = (function (x, y, z) 
    {
      var objResult = {
        
      };
      objResult.returnValue = x + y + z;
      objResult.returnVerifyResult = arguments[0] === "a" && arguments.length === 3;
      return objResult;
    });
    var NewFunc = Function.prototype.bind.call(func, {
      
    }, "a", "b", "c");
    var newInstance = new NewFunc();
    return newInstance.hasOwnProperty("returnValue") && newInstance.returnValue === "abc" && newInstance.hasOwnProperty("returnVerifyResult") && newInstance.returnVerifyResult === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  