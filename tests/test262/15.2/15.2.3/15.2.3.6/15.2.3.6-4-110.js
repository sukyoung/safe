//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function setFunc1() 
//     {
//       return 10;
//     }
//     Object.defineProperty(obj, "foo", {
//       set : setFunc1,
//       enumerable : true,
//       configurable : true
//     });
//     function setFunc2(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(obj, "foo", {
//       set : setFunc2
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", undefined, setFunc2, "setVerifyHelpProp", 
//     true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
