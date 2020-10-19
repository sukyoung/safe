QUnit.module('lodash.without');
(function () {
    QUnit.test('should return the difference of values', function (assert) {
        assert.expect(1);
        var actual = _.without([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], __num_top__, __num_top__);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test('should use strict equality to determine the values to reject', function (assert) {
        assert.expect(2);
        var object1 = { 'a': __num_top__ }, object2 = { 'b': __num_top__ }, array = [
                object1,
                object2
            ];
        assert.deepEqual(_.without(array, { 'a': __num_top__ }), array);
        assert.deepEqual(_.without(array, object1), [object2]);
    });
    QUnit.test('should remove all occurrences of each value from an array', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(_.without(array, __num_top__, __num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
}());