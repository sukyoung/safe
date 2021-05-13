QUnit.module('"Strings" category methods');
(function () {
    var stringMethods = [
        'camelCase',
        'capitalize',
        __str_top__,
        'kebabCase',
        __str_top__,
        'lowerFirst',
        'pad',
        __str_top__,
        'padStart',
        __str_top__,
        __str_top__,
        __str_top__,
        'toUpper',
        'trim',
        'trimEnd',
        'trimStart',
        'truncate',
        'unescape',
        __str_top__,
        'upperFirst'
    ];
    lodashStable.each(stringMethods, function (methodName) {
        var func = _[methodName];
        QUnit.test('`_.' + methodName + __str_top__, function (assert) {
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