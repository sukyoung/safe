function fnExists(/*arguments*/) {
    for (var i = 0; i < arguments.length; i++) {
        if (typeof (arguments[i]) !== "function") return false;
    }
    return true;
}

  function testcase() 
  {
    var foo = (function () 
    {
      
    });
    var d = delete foo;
    if (d === false && fnExists(foo))
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
