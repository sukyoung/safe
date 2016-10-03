//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     obj.verifySetFunc = "data";
//     var getFunc = (function () 
//     {
//       return obj.verifySetFunc;
//     });
//     var setFunc = (function (value) 
//     {
//       obj.verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "0", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : true,
//       configurable : true
//     });
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "0");
//     Object.defineProperty(obj, "0", {
//       value : 1001
//     });
//     var desc2 = Object.getOwnPropertyDescriptor(obj, "0");
//     return desc1.hasOwnProperty("get") && desc2.hasOwnProperty("value") && typeof desc2.get === "undefined" && typeof desc2.get === "undefined" && dataPropertyAttributesAreCorrect(obj, "0", 1001, false, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
function dataPropertyAttributesAreCorrect(obj,
                                          name,
                                          value,
                                          writable,
                                          enumerable,
                                          configurable) {
    var attributesCorrect = true;

    if (obj[name] !== value) {
        if (typeof obj[name] === "number" &&
            isNaN(obj[name]) &&
            typeof value === "number" &&
            isNaN(value)) {
            // keep empty
        } else {
            attributesCorrect = false;
        }
    }

    try {
        if (obj[name] === "oldValue") {
            obj[name] = "newValue";
        } else {
            obj[name] = "OldValue";
        }
    } catch (we) {
    }

    var overwrited = false;
    if (obj[name] !== value) {
        if (typeof obj[name] === "number" &&
            isNaN(obj[name]) &&
            typeof value === "number" &&
            isNaN(value)) {
            // keep empty
        } else {
            overwrited = true;
        }
    }
    if (overwrited !== writable) {
        attributesCorrect = false;
    }

    var enumerated = false;
    for (var prop in obj) {
        if (obj.hasOwnProperty(prop) && prop === name) {
            enumerated = true;
        }
    }

    if (enumerated !== enumerable) {
        attributesCorrect = false;
    }


    var deleted = false;

    try {
        delete obj[name];
    } catch (de) {
    }
    if (!obj.hasOwnProperty(name)) {
        deleted = true;
    }
    if (deleted !== configurable) {
        attributesCorrect = false;
    }

    return attributesCorrect;
}
