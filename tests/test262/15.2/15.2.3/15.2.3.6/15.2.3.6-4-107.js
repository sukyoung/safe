//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function getFunc() 
//     {
//       return 10;
//     }
//     function setFunc(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : getFunc,
//       set : setFunc,
//       configurable : true
//     });
//     function getFunc2() 
//     {
//       return 20;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : getFunc2
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", getFunc2, setFunc, "setVerifyHelpProp", false, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
