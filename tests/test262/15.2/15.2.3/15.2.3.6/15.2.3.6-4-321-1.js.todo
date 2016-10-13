//   TODO getter/setter
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       function getFunc() 
//       {
//         return "genericPropertyString";
//       }
//       function setFunc(value) 
//       {
//         this.helpVerifyGet = value;
//       }
//       Object.defineProperty(arguments, "genericProperty", {
//         get : getFunc,
//         set : setFunc,
//         configurable : false
//       });
//       try
// {        Object.defineProperty(arguments, "genericProperty", {
//           get : (function () 
//           {
//             return "overideGenericPropertyString";
//           })
//         });}
//       catch (e)
// {        var verifyFormal = a === 1;
//         return e instanceof TypeError && accessorPropertyAttributesAreCorrect(arguments, "genericProperty", getFunc, setFunc, "helpVerifyGet", 
//         false, 
//         false, 
//         false) && verifyFormal;}
// 
//       return false;
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
