  function testcase() 
  {
    try
{      Object.defineProperty(Object.prototype, "prop2", {
        value : 100,
        writable : false,
        configurable : true
      });
      var obj = {
        prop1 : 101,
        prop2 : 12
      };
      return obj.hasOwnProperty("prop2");}
    finally
{      delete Object.prototype.prop2;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  