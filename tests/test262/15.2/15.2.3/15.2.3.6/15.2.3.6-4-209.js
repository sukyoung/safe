//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     var setFunc = (function (value) 
//     {
//       arrObj.setVerifyHelpProp = value;
//     });
//     var getFunc = (function () 
//     {
//       return 14;
//     });
//     Object.defineProperty(arrObj, "0", {
//       get : (function () 
//       {
//         return 11;
//       }),
//       set : (function () 
//       {
//         
//       }),
//       configurable : true,
//       enumerable : true
//     });
//     Object.defineProperty(arrObj, "0", {
//       get : getFunc,
//       set : setFunc,
//       configurable : false,
//       enumerable : false
//     });
//     return accessorPropertyAttributesAreCorrect(arrObj, "0", getFunc, setFunc, "setVerifyHelpProp", false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
