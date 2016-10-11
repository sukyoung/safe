  function testcase() 
  {
    try
{      var data = "data";
      var getFunc = (function () 
      {
        return data;
      });
      var setFunc = (function (value) 
      {
        data = value;
      });
      Object.defineProperty(Object.prototype, "0", {
        get : getFunc,
        set : setFunc,
        configurable : true
      });
      var argObj = (function () 
      {
        return arguments;
      })(1);
      var verifyValue = false;
      verifyValue = (argObj[0] === 1);
      var verifyEnumerable = false;
      for(var p in argObj)
      {
        if (p === "0" && argObj.hasOwnProperty("0"))
        {
          verifyEnumerable = true;
        }
      }
      var verifyWritable = false;
      argObj[0] = 1001;
      verifyWritable = (argObj[0] === 1001);
      var verifyConfigurable = false;
      delete argObj[0];
      verifyConfigurable = argObj.hasOwnProperty("0");
      return verifyValue && verifyWritable && verifyEnumerable && ! verifyConfigurable && data === "data";}
    finally
{      delete Object.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  