//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function getFunc() 
//     {
//       return "property";
//     }
//     Object.defineProperty(obj, "property", {
//       get : getFunc,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "property", {
//         get : getFunc,
//         set : (function () 
//         {
//           
//         }),
//         configurable : false
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "property", getFunc, undefined, undefined, false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
