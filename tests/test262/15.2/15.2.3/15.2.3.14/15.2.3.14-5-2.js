  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      get : (function () 
      {
        return 1003;
      }),
      enumerable : true,
      configurable : true
    });
    var arr = Object.keys(obj);
    return arr.hasOwnProperty(0) && arr[0] === "prop";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  