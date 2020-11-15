QUnit.module('"Strings" category methods');
(function () {
    var stringMethods = [
        'camelCase',
        'capitalize',
        'escape',
        'kebabCase',
        'lowerCase',
        'lowerFirst',
        'pad',
        __str_top__,
        'padStart',
        'repeat',
        'snakeCase',
        'toLower',
        'toUpper',
        'trim',
        __str_top__,
        'trimStart',
        'truncate',
        __str_top__,
        __str_top__,
        'upperFirst'
    ];
    lodashStable.each(stringMethods, function (methodName) {
        var func = _[methodName];
        QUnit.test(__str_top__ + methodName + '` should return an empty string for empty values', function (assert) {
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