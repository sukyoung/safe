  function testcase() 
  {
    var obj = {
      
    };
    var str = new String("abc");
    str.writable = false;
    Object.defineProperties(obj, {
      property : str
    });
    obj.property = "isWritable";
    return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  