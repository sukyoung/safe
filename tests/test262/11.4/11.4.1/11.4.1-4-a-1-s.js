// TODO strict
//   function testcase() 
//   {
//   "use strict";
//     var obj = {
      
//     };
//     Object.defineProperty(obj, "prop", {
//       value : "abc",
//       configurable : false
//     });
//     try
// {      delete obj.prop;
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && obj.prop === "abc";}

//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
  
