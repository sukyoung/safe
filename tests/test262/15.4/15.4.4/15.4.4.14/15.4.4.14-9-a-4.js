  function testcase() 
  {
    var arr = {
      2 : 6.99
    };
    Object.defineProperty(arr, "length", {
      get : (function () 
      {
        delete arr[2];
        return 3;
      }),
      configurable : true
    });
    return - 1 === Array.prototype.indexOf.call(arr, 6.99);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  