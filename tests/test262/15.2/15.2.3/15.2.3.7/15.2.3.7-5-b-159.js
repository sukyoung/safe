  function testcase() 
  {
    var obj = {
      
    };
    try
{      JSON.writable = false;
      Object.defineProperties(obj, {
        property : JSON
      });
      obj.property = "isWritable";
      return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";}
    finally
{      delete JSON.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  