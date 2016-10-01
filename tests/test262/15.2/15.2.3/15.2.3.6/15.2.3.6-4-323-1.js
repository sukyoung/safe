//   TODO getter/setter
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       function setFunc(value) 
//       {
//         this.genericPropertyString = value;
//       }
//       Object.defineProperty(arguments, "genericProperty", {
//         set : setFunc,
//         enumerable : true,
//         configurable : false
//       });
//       try
// {        Object.defineProperty(arguments, "genericProperty", {
//           enumerable : false
//         });}
//       catch (e)
// {        verifyFormal = c === 3;
//         return e instanceof TypeError && accessorPropertyAttributesAreCorrect(arguments, "genericProperty", undefined, setFunc, "genericPropertyString", 
//         true, 
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
