  function testcase() 
  {
    var obj = {
      
    };
    var func = (function (a, b) 
    {
      return a + b;
    });
    func.writable = false;
    Object.defineProperties(obj, {
      property : func
    });
    obj.property = "isWritable";
    return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  