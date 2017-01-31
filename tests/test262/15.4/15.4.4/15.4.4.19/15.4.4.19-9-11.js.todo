  function testcase() 
  {
    function Foo() 
    {
      
    }
    Foo.prototype = [1, 2, 3, ];
    var f = new Foo();
    var o = {
      toString : (function () 
      {
        return '0';
      })
    };
    f.length = o;
    function cb() 
    {
      
    }
    var a = Array.prototype.map.call(f, cb);
    if (Array.isArray(a) && a.length === 0)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  