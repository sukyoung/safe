//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function setFunc(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(obj, "foo", {
//       set : setFunc,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "foo", {
//         set : setFunc
//       });
//       return accessorPropertyAttributesAreCorrect(obj, "foo", undefined, setFunc, "setVerifyHelpProp", 
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
