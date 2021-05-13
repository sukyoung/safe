QUnit.module('"Strings" category methods');
(function () {
    var stringMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    lodashStable.each(stringMethods, function (methodName) {
        var func = _[methodName];
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var values = [
                    ,
                    null,
                    undefined,
                    __str_top__
                ], expected = lodashStable.map(values, stubString);
            var actual = lodashStable.map(values, function (value, index) {
                return index ? func(value) : func();
            });
            assert.deepEqual(actual, expected);
        });
    });
}());