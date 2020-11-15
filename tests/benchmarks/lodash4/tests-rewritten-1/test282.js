QUnit.module('lodash.without');
(function () {
    QUnit.test('should return the difference of values', function (assert) {
        assert.expect(1);
        var actual = _.without([
            2,
            1,
            2,
            3
        ], 1, 2);
        assert.deepEqual(actual, [3]);
    });
    QUnit.test('should use strict equality to determine the values to reject', function (assert) {
        assert.expect(2);
        var object1 = { 'a': 1 }, object2 = { 'b': 2 }, array = [
                object1,
                object2
            ];
        assert.deepEqual(_.without(array, { 'a': __num_top__ }), array);
        assert.deepEqual(_.without(array, object1), [object2]);
    });
    QUnit.test('should remove all occurrences of each value from an array', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3,
            1,
            2,
            3
        ];
        assert.deepEqual(_.without(array, 1, 2), [
            3,
            3
        ]);
    });
}());