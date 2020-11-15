QUnit.module('stub methods');
lodashStable.each([
    __str_top__,
    'stubTrue',
    'stubFalse',
    'stubArray',
    __str_top__,
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
            __str_top__
        ],
        'stubObject': [
            {},
            __str_top__
        ],
        'stubString': [
            __str_top__,
            __str_top__
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
    var values = Array(__num_top__).concat(empties, true, 1, 'a'), expected = lodashStable.map(values, lodashStable.constant(pair[0]));
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