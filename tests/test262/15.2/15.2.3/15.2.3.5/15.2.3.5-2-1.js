  function testcase() 
  {
    function base() 
    {
      
    }
    var b = new base();
    var prop = new Object();
    var d = Object.create(b);
    if (typeof d === 'object')
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  