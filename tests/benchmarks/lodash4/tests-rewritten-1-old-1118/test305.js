QUnit.module('"Strings" category methods');
(function () {
    var stringMethods = [
        'camelCase',
        'capitalize',
        'escape',
        'kebabCase',
        'lowerCase',
        __str_top__,
        'pad',
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
        'upperFirst'
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