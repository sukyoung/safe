  function testcase() 
  {
    try
{      JSON.value = "JSONValue";
      var newObj = Object.create({
        
      }, {
        prop : JSON
      });
      return newObj.prop === "JSONValue";}
    finally
{      delete JSON.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  