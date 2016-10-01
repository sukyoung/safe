//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     function getFunc() 
//     {
//       return 12;
//     }
//     function setFunc(value) 
//     {
//       arrObj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(arrObj, "property", {
//       get : (function () 
//       {
//         return 24;
//       }),
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "property", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : false,
//       configurable : false
//     });
//     return accessorPropertyAttributesAreCorrect(arrObj, "property", getFunc, setFunc, "setVerifyHelpProp", 
//     false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
