  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "a", {
      get : (function () 
      {
        return "a";
      }),
      configurable : true
    });
    var result = Object.getOwnPropertyNames(obj);
    return result[0] === "a";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  