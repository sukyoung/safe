  function testcase() 
  {
    var obj = {
      
    };
    var func = (function (a, b, c) 
    {
      return arguments;
    });
    Object.defineProperties(obj, {
      property : {
        writable : func(1, true, "a")
      }
    });
    obj.property = "isWritable";
    return obj.property === "isWritable";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  