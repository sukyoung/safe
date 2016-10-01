//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var setFunc = (function (value) 
//     {
//       obj.setVerifyHelpProp = value;
//     });
//     var getFunc = (function () 
//     {
//       return 14;
//     });
//     Object.defineProperty(obj, "property", {
//       get : (function () 
//       {
//         return 11;
//       }),
//       set : (function (value) 
//       {
//         
//       }),
//       configurable : true,
//       enumerable : true
//     });
//     Object.defineProperty(obj, "property", {
//       get : getFunc,
//       set : setFunc,
//       configurable : false,
//       enumerable : false
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "property", getFunc, setFunc, "setVerifyHelpProp", 
//     false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
