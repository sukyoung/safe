QUnit.module('lodash.intersectionWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': __num_top__,
                    'y': __num_top__
                },
                {
                    'x': __num_top__,
                    'y': __num_top__
                }
            ], others = [
                {
                    'x': __num_top__,
                    'y': __num_top__
                },
                {
                    'x': 1,
                    'y': __num_top__
                }
            ], actual = _.intersectionWith(objects, others, lodashStable.isEqual);
        assert.deepEqual(actual, [objects[0]]);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var array = [-__num_top__], largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubZero), others = [
                [__num_top__],
                largeArray
            ], expected = lodashStable.map(others, lodashStable.constant([__str_top__]));
        var actual = lodashStable.map(others, function (other) {
            return lodashStable.map(_.intersectionWith(array, other, lodashStable.eq), lodashStable.toString);
        });
        assert.deepEqual(actual, expected);
    });
}());