  function testcase() 
  {
    try
{      __Global.writable = true;
      var newObj = Object.create({
        
      }, {
        prop : __Global
      });
      var beforeWrite = (newObj.hasOwnProperty("prop") && typeof (newObj.prop) === "undefined");
      newObj.prop = "isWritable";
      var afterWrite = (newObj.prop === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete __Global.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
