  function testcase() 
  {
    var obj = {
      
    };
    try
{      @Global.writable = false;
      Object.defineProperties(obj, {
        property : @Global
      });
      obj.property = "isWritable";
      return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";}
    finally
{      delete @Global.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
