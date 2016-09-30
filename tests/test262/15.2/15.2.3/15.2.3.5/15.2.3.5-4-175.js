  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.value = "ArgValue";
    var newObj = Object.create({
      
    }, {
      prop : argObj
    });
    return newObj.prop === "ArgValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  