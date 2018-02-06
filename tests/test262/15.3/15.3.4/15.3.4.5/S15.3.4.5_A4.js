  var __result1 = true;
  function foo() 
  {
    
  }
  var b = foo.bind(33, 44);
  foo.apply = (function () 
  {
    __result1 = false;
  });
  b(55, 66);

var __expect1 = true;
