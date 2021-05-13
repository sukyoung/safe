QUnit.module('stub methods');
lodashStable.each([
    __str_top__,
    'stubTrue',
    'stubFalse',
    __str_top__,
    'stubObject',
    'stubString'
], function (methodName) {
    var func = _[methodName];
    var pair = {
        'stubArray': [
            [],
            __str_top__
        ],
        'stubFalse': [
            __bool_top__,
            __str_top__
        ],
        'stubObject': [
            {},
            __str_top__
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
            '`undefined`'
        ]
    }[methodName];
    var values = Array(2).concat(empties, __bool_top__, __num_top__, 'a'), expected = lodashStable.map(values, lodashStable.constant(pair[0]));
    QUnit.test('`_.' + methodName + '` should return ' + pair[__num_top__], function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(values, function (value, index) {
            if (index < __num_top__) {
                return index ? func.call({}) : func();
            }
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
});