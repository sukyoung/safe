//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     function setFunc(value) 
//     {
//       arrObj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(arrObj, "1", {
//       get : undefined,
//       set : setFunc,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(arrObj, "1", {
//         get : undefined
//       });
//       return accessorPropertyAttributesAreCorrect(arrObj, "1", undefined, setFunc, "setVerifyHelpProp", 
//       false, 
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
