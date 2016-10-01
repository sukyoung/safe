//   TODO getter/setter
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       Object.defineProperty(arguments, "genericProperty", {
//         get : (function () 
//         {
//           return 1001;
//         }),
//         set : (function (value) 
//         {
//           this.testgetFunction1 = value;
//         }),
//         enumerable : true,
//         configurable : true
//       });
//       function getFunc() 
//       {
//         return "getFunctionString";
//       }
//       function setFunc(value) 
//       {
//         this.testgetFunction = value;
//       }
//       Object.defineProperty(arguments, "genericProperty", {
//         get : getFunc,
//         set : setFunc,
//         enumerable : false,
//         configurable : false
//       });
//       var verifyFormal = c === 3;
//       return accessorPropertyAttributesAreCorrect(arguments, "genericProperty", getFunc, setFunc, "testgetFunction", 
//       false, 
//       false) && verifyFormal;
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
