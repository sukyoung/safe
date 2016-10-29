  function testcase() 
  {
    var obj = {
      prop : "abc"
    };
    try
{      Object.prototype.verifyThis = "verifyThis";
      var func = (function () 
      {
        return new Boolean(arguments.length === 0 && Object.prototype.toString.call(this) === "[object Object]" && this.verifyThis === "verifyThis");
      });
      var NewFunc = Function.prototype.bind.call(func, obj);
      var newInstance = new NewFunc();
      return newInstance.valueOf();}
    finally
{      delete Object.prototype.verifyThis;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  