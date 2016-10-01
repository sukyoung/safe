//   TODO getter/setter
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       delete arguments[0];
//       function getFunc() 
//       {
//         return 10;
//       }
//       function setFunc(value) 
//       {
//         this.setVerifyHelpProp = value;
//       }
//       Object.defineProperty(arguments, "0", {
//         get : getFunc,
//         set : setFunc,
//         enumerable : true,
//         configurable : true
//       });
//       var verifyFormal = a === 0;
//       return accessorPropertyAttributesAreCorrect(arguments, "0", getFunc, setFunc, "setVerifyHelpProp", 
//       true, 
//       true) && verifyFormal;
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
