//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 10,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         foo : {
//           writable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && dataPropertyAttributesAreCorrect(obj, "foo", 10, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
