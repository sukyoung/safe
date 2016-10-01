  function testcase() 
  {
    var obj = {
      
    };
    try
{      Number.prototype.value = "Number";
      var numObj = new Number(- 2);
      Object.defineProperty(obj, "property", numObj);
      return obj.property === "Number";}
    finally
{      delete Number.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  