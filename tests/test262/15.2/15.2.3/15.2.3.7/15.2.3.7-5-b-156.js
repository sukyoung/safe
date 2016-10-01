  function testcase() 
  {
    var obj = {
      
    };
    try
{      Math.writable = false;
      Object.defineProperties(obj, {
        property : Math
      });
      obj.property = "isWritable";
      return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";}
    finally
{      delete Math.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  