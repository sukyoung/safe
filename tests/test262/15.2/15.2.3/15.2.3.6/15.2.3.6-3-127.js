  function testcase() 
  {
    var obj = {
      
    };
    var attr = {
      writable : true
    };
    Object.defineProperty(obj, "property", attr);
    return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  