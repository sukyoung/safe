"use strict";
  function foo() 
  {
    
  }
  var names = Object.getOwnPropertyNames(foo);
  for(var i = 0, len = names.length;i < len;i++)
  {
    {
      var __result1 = ! foo.hasOwnProperty(names[i]);
      var __expect1 = false;
    }
  }
  