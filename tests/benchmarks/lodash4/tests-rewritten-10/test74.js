QUnit.module('forOwn methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            __str_top__
        ]);
    });
});