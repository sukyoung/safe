  function testcase() 
  {
    var isNotConfigurable = false;
    try
{      var newObj = Object.create({
        
      }, {
        prop : {
          value : 1001,
          writable : true,
          enumerable : true
        }
      });
      var hasProperty = newObj.hasOwnProperty("prop");
      delete newObj.prop;
      isNotConfigurable = newObj.hasOwnProperty("prop");
      return hasProperty && isNotConfigurable;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  