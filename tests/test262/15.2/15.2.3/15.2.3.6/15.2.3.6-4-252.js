//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     function setFunc(value) 
//     {
//       arrObj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(arrObj, "1", {
//       set : setFunc
//     });
//     try
// {      Object.defineProperty(arrObj, "1", {
//         set : (function () 
//         {
//           
//         })
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && accessorPropertyAttributesAreCorrect(arrObj, "1", undefined, setFunc, "setVerifyHelpProp", 
//       false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
