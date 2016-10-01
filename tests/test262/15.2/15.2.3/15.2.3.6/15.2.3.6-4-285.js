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
//       get : getFunc,
//       set : setFunc
//     });
//     try
// {      Object.defineProperty(arrObj, "property", {
//         get : (function () 
//         {
//           return 36;
//         })
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && accessorPropertyAttributesAreCorrect(arrObj, "property", getFunc, setFunc, "setVerifyHelpProp", 
//       false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
