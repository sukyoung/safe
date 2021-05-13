QUnit.module('lodash.shuffle');
(function () {
    var array = [
            1,
            __num_top__,
            3
        ], object = {
            'a': 1,
            'b': 2,
            'c': 3
        };
    QUnit.test('should return a new array', function (assert) {
        assert.expect(1);
        assert.notStrictEqual(_.shuffle(array), array);
    });
    QUnit.test('should contain the same elements after a collection is shuffled', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.shuffle(array).sort(), array);
        assert.deepEqual(_.shuffle(object).sort(), array);
    });
    QUnit.test('should shuffle small collections', function (assert) {
        assert.expect(1);
        var actual = lodashStable.times(1000, function (assert) {
            return _.shuffle([
                1,
                2
            ]);
        });
        assert.deepEqual(lodashStable.sortBy(lodashStable.uniqBy(actual, String), '0'), [
            [
                1,
                2
            ],
            [
                2,
                1
            ]
        ]);
    });
    QUnit.test('should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.shuffle(1), []);
    });
}());