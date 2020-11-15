QUnit.module('stub methods');
lodashStable.each([
    'noop',
    'stubTrue',
    'stubFalse',
    'stubArray',
    'stubObject',
    __str_top__
], function (methodName) {
    var func = _[methodName];
    var pair = {
        'stubArray': [
            [],
            'an empty array'
        ],
        'stubFalse': [
            false,
            __str_top__
        ],
        'stubObject': [
            {},
            'an empty object'
        ],
        'stubString': [
            '',
            'an empty string'
        ],
        'stubTrue': [
            true,
            '`true`'
        ],
        'noop': [
            undefined,
            __str_top__
        ]
    }[methodName];
    var values = Array(2).concat(empties, __bool_top__, 1, 'a'), expected = lodashStable.map(values, lodashStable.constant(pair[0]));
    QUnit.test('`_.' + methodName + '` should return ' + pair[__num_top__], function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(values, function (value, index) {
            if (index < 2) {
                return index ? func.call({}) : func();
            }
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
});