  function testcase() 
  {
    var obj = {
      
    };
    try
{      __Global.writable = false;
      Object.defineProperties(obj, {
        property : __Global
      });
      obj.property = "isWritable";
      return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";}
    finally
{      delete __Global.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
