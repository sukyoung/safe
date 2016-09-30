  function testcase() 
  {
    var Func = (function (a, b) 
    {
      return a + b;
    });
    var fun = new Func();
    fun.value = "FunValue";
    var newObj = Object.create({
      
    }, {
      prop : fun
    });
    return newObj.prop === "FunValue";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  