  function testcase() 
  {
    var oldArray = Array;
    Array = (function () 
    {
      throw new Error("invoke customer defined Array!");
    });
    var obj = {
      
    };
    try
{      var result = Object.getOwnPropertyNames(obj);
      return Object.prototype.toString.call(result) === "[object Array]";}
    catch (ex)
{      return false;}

    finally
{      Array = oldArray;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  