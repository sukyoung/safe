QUnit.module('lodash.assignWith and lodash.assignInWith');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func({
            'a': __num_top__,
            'b': __num_top__
        }, {
            'a': 3,
            'c': __num_top__
        }, function (a, b) {
            return a === undefined ? b : a;
        });
        assert.deepEqual(actual, {
            'a': __num_top__,
            'b': __num_top__,
            'c': 3
        });
    });
    QUnit.test(__str_top__ + methodName + '` should work with a `customizer` that returns `undefined`', function (assert) {
        assert.expect(1);
        var expected = { 'a': __num_top__ };
        assert.deepEqual(func({}, expected, noop), expected);
    });
});