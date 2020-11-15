QUnit.module('"Strings" category methods');
(function () {
    var stringMethods = [
        'camelCase',
        'capitalize',
        'escape',
        'kebabCase',
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        'padStart',
        'repeat',
        __str_top__,
        'toLower',
        'toUpper',
        'trim',
        __str_top__,
        __str_top__,
        'truncate',
        __str_top__,
        'upperCase',
        'upperFirst'
    ];
    lodashStable.each(stringMethods, function (methodName) {
        var func = _[methodName];
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var values = [
                    ,
                    null,
                    undefined,
                    ''
                ], expected = lodashStable.map(values, stubString);
            var actual = lodashStable.map(values, function (value, index) {
                return index ? func(value) : func();
            });
            assert.deepEqual(actual, expected);
        });
    });
}());