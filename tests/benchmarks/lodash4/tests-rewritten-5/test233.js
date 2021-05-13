QUnit.module('stub methods');
lodashStable.each([
    'noop',
    'stubTrue',
    'stubFalse',
    'stubArray',
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
            false,
            '`false`'
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
            __bool_top__,
            '`true`'
        ],
        'noop': [
            undefined,
            __str_top__
        ]
    }[methodName];
    var values = Array(2).concat(empties, true, 1, 'a'), expected = lodashStable.map(values, lodashStable.constant(pair[__num_top__]));
    QUnit.test('`_.' + methodName + __str_top__ + pair[1], function (assert) {
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