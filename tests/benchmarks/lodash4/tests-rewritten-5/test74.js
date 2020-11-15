QUnit.module('forOwn methods');
lodashStable.each([
    'forOwn',
    'forOwnRight'
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '0': 'zero',
                '1': 'one',
                'length': 2
            }, props = [];
        func(object, function (value, prop) {
            props.push(prop);
        });
        assert.deepEqual(props.sort(), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
});