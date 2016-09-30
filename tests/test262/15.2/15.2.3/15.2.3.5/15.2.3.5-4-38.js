  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.prop = {
      value : 12,
      enumerable : true
    };
    var newObj = Object.create({
      
    }, argObj);
    return newObj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  