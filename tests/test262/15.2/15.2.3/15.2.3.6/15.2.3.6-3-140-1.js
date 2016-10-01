  function testcase() 
  {
    var obj = {
      
    };
    try
{      Array.prototype.value = "Array";
      var arrObj = [1, 2, 3, ];
      Object.defineProperty(obj, "property", arrObj);
      return obj.property === "Array";}
    finally
{      delete Array.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  