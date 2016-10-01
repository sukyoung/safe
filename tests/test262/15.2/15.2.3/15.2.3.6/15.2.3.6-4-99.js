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
//     function getFunc() 
//     {
//       return 10;
//     }
//     try
// {      Object.defineProperty(obj, "foo", {
//         get : getFunc,
//         set : setFunc
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "foo", undefined, setFunc, "setVerifyHelpProp", 
//       false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
