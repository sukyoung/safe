//   TODO getter/setter
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     function getFun() 
//     {
//       return "genericPropertyString";
//     }
//     function setFun(value) 
//     {
//       arg.verifySetFun = value;
//     }
//     Object.defineProperty(arg, "genericProperty", {
//       get : getFun,
//       set : setFun,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arg, {
//         "genericProperty" : {
//           get : (function () 
//           {
//             return "overideGenericPropertyString";
//           })
//         }
//       });
//       return false;}
//     catch (ex)
// {      return ex instanceof TypeError && accessorPropertyAttributesAreCorrect(arg, "genericProperty", getFun, setFun, "verifySetFun", 
//       false, 
//       false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
