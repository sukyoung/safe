  function testcase() 
  {
    var obj = {
      
    };
    var arr = [1, 2, 3, ];
    arr.writable = false;
    Object.defineProperties(obj, {
      property : arr
    });
    obj.property = "isWritable";
    return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  