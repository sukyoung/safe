//   TODO getter/setter
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     Object.defineProperty(arg, "genericProperty", {
//       get : (function () 
//       {
//         return 1001;
//       }),
//       set : (function (value) 
//       {
//         arg.testGetFunction1 = value;
//       }),
//       enumerable : true,
//       configurable : true
//     });
//     function getFun() 
//     {
//       return "getFunctionString";
//     }
//     function setFun(value) 
//     {
//       arg.testGetFunction = value;
//     }
//     Object.defineProperties(arg, {
//       "genericProperty" : {
//         get : getFun,
//         set : setFun,
//         enumerable : false,
//         configurable : false
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arg, "genericProperty", getFun, setFun, "testGetFunction", 
//     false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
