  function testcase() 
  {
    var obj = {
      "a" : "a"
    };
    var result = Object.getOwnPropertyNames(obj);
    try
{      var beforeOverride = (result[0] === "a");
      result[0] = "b";
      var afterOverride = (result[0] === "b");
      return beforeOverride && afterOverride;}
    catch (ex)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  