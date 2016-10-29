function testcase() {
        var errObj = new Error("ErrorMessage");
        return errObj.toString() === "Error: ErrorMessage";
    }
var __result1 = testcase()
var __expect1 = true

