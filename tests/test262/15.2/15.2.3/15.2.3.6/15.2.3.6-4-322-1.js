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
//         configurable : false
//       });
//       try
// {        Object.defineProperty(arguments, "genericProperty", {
//           set : (function (value) 
//           {
//             this.genericPropertyString1 = value;
//           })
//         });}
//       catch (e)
// {        return e instanceof TypeError && accessorPropertyAttributesAreCorrect(arguments, "genericProperty", undefined, setFunc, "genericPropertyString", 
//         false, 
//         false, 
//         false);}
// 
//       return false;
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
