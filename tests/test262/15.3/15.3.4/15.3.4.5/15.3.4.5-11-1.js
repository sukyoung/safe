  function testcase() 
  {
    var foo = (function () 
    {
      
    });
    try
{      Function.prototype.property = 12;
      var obj = foo.bind({
        
      });
      return obj.property === 12;}
    finally
{      delete Function.prototype.property;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  