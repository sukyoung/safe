QUnit.module('forOwn methods');
lodashStable.each([
    'forOwn',
    'forOwnRight'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should iterate over `length` properties', function (assert) {
        assert.expect(1);
        var object = {
                '0': 'zero',
                '1': 'one',
                'length': __num_top__
            }, props = [];
        func(object, function (value, prop) {
            props.push(prop);
        });
        assert.deepEqual(props.sort(), [
            '0',
            '1',
            'length'
        ]);
    });
});