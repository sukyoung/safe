var obj = {
    toString: function () {return this;}
};
try {
    result = obj + "abc";
} catch (e) {
    result = "def";
}

