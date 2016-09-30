  function testcase() 
  {
    try
{      var newObj = Object.create({
        
      }, {
        prop : {
          writable : true,
          configurable : true,
          enumerable : true
        }
      });
      return newObj.hasOwnProperty("prop") && newObj.prop === undefined;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  