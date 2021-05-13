QUnit.module('forOwn methods');
lodashStable.each([
    'forOwn',
    'forOwnRight'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should iterate over `length` properties', function (assert) {
        assert.expect(1);
        var object = {
                '0': __str_top__,
                '1': __str_top__,
                'length': __num_top__
            }, props = [];
        func(object, function (value, prop) {
            props.push(prop);
        });
        assert.deepEqual(props.sort(), [
            __str_top__,
            __str_top__,
            'length'
        ]);
    });
});