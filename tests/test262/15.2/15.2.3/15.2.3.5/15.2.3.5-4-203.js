  function testcase() 
  {
    try
{      @Global.writable = true;
      var newObj = Object.create({
        
      }, {
        prop : @Global
      });
      var beforeWrite = (newObj.hasOwnProperty("prop") && typeof (newObj.prop) === "undefined");
      newObj.prop = "isWritable";
      var afterWrite = (newObj.prop === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete @Global.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
