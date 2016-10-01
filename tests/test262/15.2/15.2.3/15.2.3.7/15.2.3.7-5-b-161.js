  function testcase() 
  {
    var obj = {
      
    };
    var func = (function (a, b) 
    {
      arguments.writable = false;
      Object.defineProperties(obj, {
        property : arguments
      });
      obj.property = "isWritable";
      return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
    });
    return func();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  