QUnit.module('stub methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
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
            __str_top__,
            __str_top__
        ],
        'stubTrue': [
            __bool_top__,
            __str_top__
        ],
        'noop': [
            undefined,
            __str_top__
        ]
    }[methodName];
    var values = Array(__num_top__).concat(empties, __bool_top__, __num_top__, __str_top__), expected = lodashStable.map(values, lodashStable.constant(pair[__num_top__]));
    QUnit.test(__str_top__ + methodName + __str_top__ + pair[__num_top__], function (assert) {
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