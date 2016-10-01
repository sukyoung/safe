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

  function testcase() 
  {
    var obj = {
   
    };
    Object.defineProperty(obj, "foo1", {
      value : 10,
      writable : false,
      enumerable : true,
      configurable : false
    });
    Object.defineProperty(obj, "foo2", {
      value : 20,
      writable : true,
      enumerable : false,
      configurable : false
    });
    Object.freeze(obj);
    var desc1 = Object.getOwnPropertyDescriptor(obj, "foo1");
    var desc2 = Object.getOwnPropertyDescriptor(obj, "foo2");
    return dataPropertyAttributesAreCorrect(obj, "foo1", 10, false, true, false) && dataPropertyAttributesAreCorrect(obj, "foo2", 20, false, false, false) && desc1.configurable === false && desc1.writable === false && desc2.configurable === false && desc2.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }

