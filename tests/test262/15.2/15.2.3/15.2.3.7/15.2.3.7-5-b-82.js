  function testcase() 
  {
    var obj = {
      
    };
    var arg;
    (function fun() 
    {
      arg = arguments;
    })();
    arg.configurable = true;
    Object.defineProperties(obj, {
      prop : arg
    });
    var result1 = obj.hasOwnProperty("prop");
    delete obj.prop;
    var result2 = obj.hasOwnProperty("prop");
    return result1 === true && result2 === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  