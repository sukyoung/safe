  function testcase() 
  {
    var proto = {
      
    };
    Object.defineProperty(proto, "foo", {
      value : 12,
      configurable : true
    });
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var obj = new ConstructFun();
    Object.defineProperty(obj, "foo", {
      value : 11,
      configurable : false
    });
    try
{      Object.defineProperty(obj, "foo", {
        configurable : true
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && obj.foo === 11;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  