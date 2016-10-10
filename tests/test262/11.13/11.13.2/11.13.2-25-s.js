// TODO strict
//   function testcase() 
//   {
//   "use strict";
//     var obj = {
      
//     };
//     Object.defineProperty(obj, "prop", {
//       value : 10,
//       writable : false,
//       enumerable : true,
//       configurable : true
//     });
//     try
// {      obj.prop %= 20;
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && obj.prop === 10;}

//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
