  function testcase() 
  {
    var obj = {
      
    };
    var strObj = new String("abc");
    strObj.value = "String";
    Object.defineProperty(obj, "property", strObj);
    return obj.property === "String";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  