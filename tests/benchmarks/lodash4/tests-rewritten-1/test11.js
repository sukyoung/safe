QUnit.module('lodash.assignWith and lodash.assignInWith');
lodashStable.each([
    'assignWith',
    'assignInWith'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should work with a `customizer` callback', function (assert) {
        assert.expect(1);
        var actual = func({
            'a': 1,
            'b': 2
        }, {
            'a': 3,
            'c': 3
        }, function (a, b) {
            return a === undefined ? b : a;
        });
        assert.deepEqual(actual, {
            'a': 1,
            'b': 2,
            'c': 3
        });
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = { 'a': 1 };
        assert.deepEqual(func({}, expected, noop), expected);
    });
});