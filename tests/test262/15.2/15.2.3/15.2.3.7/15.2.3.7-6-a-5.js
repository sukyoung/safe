//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function getFunc() 
//     {
//       return 11;
//     }
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         prop : {
//           value : 12,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "prop", getFunc, undefined, undefined, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
