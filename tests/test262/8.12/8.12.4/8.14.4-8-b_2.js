// TODO strict
//   function testcase() 
//   {
//   "use strict";
//     function foo() 
//     {
      
//     }
//     ;
//     Object.defineProperty(foo.prototype, "bar", {
//       value : "unwritable"
//     });
//     var o = new foo();
//     try
// {      o.bar = "overridden";
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && (o.bar === "unwritable");}

//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }

