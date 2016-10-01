//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperties(arr, {
//       "0" : {
//         get : (function () 
//         {
//           return 11;
//         }),
//         set : (function () 
//         {
//           
//         }),
//         configurable : true,
//         enumerable : true
//       }
//     });
//     var setFun = (function (value) 
//     {
//       arr.setVerifyHelpProp = value;
//     });
//     var getFun = (function () 
//     {
//       return 14;
//     });
//     Object.defineProperties(arr, {
//       "0" : {
//         get : getFun,
//         set : setFun,
//         configurable : false,
//         enumerable : false
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arr, "0", getFun, setFun, "setVerifyHelpProp", false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
