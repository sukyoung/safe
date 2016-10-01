//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     function set_func(value) 
//     {
//       arr.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(arr, "0", {
//       set : set_func
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           set : set_func
//         }
//       });
//       return accessorPropertyAttributesAreCorrect(arr, "0", undefined, set_func, "setVerifyHelpProp", false, 
//       false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
