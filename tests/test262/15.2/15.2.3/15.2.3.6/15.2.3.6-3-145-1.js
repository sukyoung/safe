  function testcase() 
  {
    var obj = {
      
    };
    try
{      Date.prototype.value = "Date";
      var dateObj = new Date();
      Object.defineProperty(obj, "property", dateObj);
      return obj.property === "Date";}
    finally
{      delete Date.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  