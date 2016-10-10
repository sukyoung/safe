  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "0", {
      value : 1001,
      writable : false,
      configurable : true
    });
    Object.defineProperty(obj, "1", {
      value : 1003,
      writable : false,
      configurable : false
    });
    try
{      Object.defineProperties(obj, {
        0 : {
          value : 1002
        },
        1 : {
          value : 1004
        }
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "0", 1002, false, false, true) && dataPropertyAttributesAreCorrect(obj, "1", 1003, false, false, false);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
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
