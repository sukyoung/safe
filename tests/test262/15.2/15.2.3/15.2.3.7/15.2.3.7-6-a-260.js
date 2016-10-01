//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     function set_fun(value) 
//     {
//       arr.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(arr, "0", {
//       set : set_fun,
//       enumerable : true,
//       configurable : true
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           enumerable : false
//         }
//       });
//       return accessorPropertyAttributesAreCorrect(arr, "0", undefined, set_fun, "setVerifyHelpProp", false, 
//       true);}
//     catch (ex)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
