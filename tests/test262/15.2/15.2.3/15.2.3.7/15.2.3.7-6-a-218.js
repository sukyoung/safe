//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     var obj1 = {
//       length : 10
//     };
//     Object.defineProperty(arr, "0", {
//       value : obj1
//     });
//     var properties = {
//       "0" : {
//         value : obj1
//       }
//     };
//     try
// {      Object.defineProperties(arr, properties);
//       return dataPropertyAttributesAreCorrect(arr, "0", obj1, false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
