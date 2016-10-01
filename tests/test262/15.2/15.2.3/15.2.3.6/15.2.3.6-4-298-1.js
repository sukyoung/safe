//   TODO getter/setter
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       function getFunc() 
//       {
//         return 10;
//       }
//       Object.defineProperty(arguments, "0", {
//         get : getFunc,
//         set : undefined,
//         enumerable : false,
//         configurable : false
//       });
//       function setFunc(value) 
//       {
//         this.setVerifyHelpProp = value;
//       }
//       try
// {        Object.defineProperty(arguments, "0", {
//           set : setFunc
//         });}
//       catch (e)
// {        var verifyFormal = a === 0;
//         return e instanceof TypeError && accessorPropertyAttributesAreCorrect(arguments, "0", getFunc, undefined, undefined, false, 
//         false) && verifyFormal;}
// 
//       return false;
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
