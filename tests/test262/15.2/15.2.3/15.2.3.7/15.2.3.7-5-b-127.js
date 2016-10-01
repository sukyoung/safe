  function testcase() 
  {
    var obj = {
      
    };
    var str = new String("abc");
    str.value = "String";
    Object.defineProperties(obj, {
      property : str
    });
    return obj.property === "String";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  