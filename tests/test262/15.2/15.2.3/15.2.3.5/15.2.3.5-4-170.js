  function testcase() 
  {
    try
{      Math.value = "MathValue";
      var newObj = Object.create({
        
      }, {
        prop : Math
      });
      return newObj.prop === "MathValue";}
    finally
{      delete Math.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  