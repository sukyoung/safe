QUnit.module('lodash.shuffle');
(function () {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ], object = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
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
        var actual = lodashStable.times(__num_top__, function (assert) {
            return _.shuffle([
                __num_top__,
                __num_top__
            ]);
        });
        assert.deepEqual(lodashStable.sortBy(lodashStable.uniqBy(actual, String), __str_top__), [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.shuffle(__num_top__), []);
    });
}());