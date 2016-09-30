  function testcase() 
  {
    function base() 
    {
      
    }
    var b = new base();
    var prop = new Object();
    var d = Object.create(b, {
      "x" : {
        value : true,
        writable : false
      },
      "y" : {
        value : "str",
        writable : false
      }
    });
    if (Object.getPrototypeOf(d) === b && b.isPrototypeOf(d) === true && d.x === true && d.y === "str" && b.x === undefined && b.y === undefined)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  