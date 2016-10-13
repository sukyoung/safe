  function testcase() 
  {
    var foo = (function () 
    {
      
    });
    var obj = foo.bind({
      
    });
    try
{      Object.defineProperty(Function.prototype, "property", {
        get : (function () 
        {
          return 3;
        }),
        configurable : true
      });
      Object.defineProperty(obj, "property", {
        set : (function () 
        {
          
        })
      });
      return typeof (obj.property) === "undefined";}
    finally
{      delete Function.prototype.property;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  