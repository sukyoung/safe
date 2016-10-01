//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     function setFunc(value) 
//     {
//       arrObj.setVerifyHelpProp = value;
//     }
//     function getFunc() 
//     {
//       return 12;
//     }
//     Object.defineProperty(arrObj, "1", {
//       get : (function () 
//       {
//         return 6;
//       }),
//       set : setFunc,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "1", {
//       get : getFunc,
//       enumerable : false,
//       configurable : false
//     });
//     return accessorPropertyAttributesAreCorrect(arrObj, "1", getFunc, setFunc, "setVerifyHelpProp", false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
