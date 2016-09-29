  function testcase() 
  {
    try
{      var str = new String("abc");
      String.prototype.protoProperty = "protoString";
      var result = Object.getOwnPropertyNames(str);
      for(var p in result)
      {
        if (result[p] === "protoProperty")
        {
          return false;
        }
      }
      return true;}
    finally
{      delete String.prototype.protoProperty;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  