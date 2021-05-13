QUnit.module('"Strings" category methods');
(function () {
    var stringMethods = [
        __str_top__,
        __str_top__,
        'escape',
        'kebabCase',
        'lowerCase',
        'lowerFirst',
        __str_top__,
        'padEnd',
        'padStart',
        'repeat',
        'snakeCase',
        'toLower',
        'toUpper',
        'trim',
        'trimEnd',
        'trimStart',
        'truncate',
        'unescape',
        'upperCase',
        __str_top__
    ];
    lodashStable.each(stringMethods, function (methodName) {
        var func = _[methodName];
        QUnit.test('`_.' + methodName + '` should return an empty string for empty values', function (assert) {
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