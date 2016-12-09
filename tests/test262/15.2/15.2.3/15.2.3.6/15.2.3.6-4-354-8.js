  function testcase() 
  {
    var obj = @Global;
    try
{      Object.defineProperty(obj, "prop", {
        value : 2010,
        writable : false,
        enumerable : true,
        configurable : true
      });
      var valueVerify = (obj.prop === 2010);
      obj.prop = 1001;
      return valueVerify && obj.prop === 2010;}
    finally
{      delete obj.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
