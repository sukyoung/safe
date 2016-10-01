//   TODO getter/setter
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
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
//         enumerable : true,
//         configurable : true
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arg, "genericProperty", getFun, setFun, "testGetFunction", 
//     true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
