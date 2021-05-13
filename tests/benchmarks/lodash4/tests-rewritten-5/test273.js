QUnit.module('lodash.uniqWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': __num_top__,
                    'y': 2
                },
                {
                    'x': 2,
                    'y': 1
                },
                {
                    'x': 1,
                    'y': __num_top__
                }
            ], actual = _.uniqWith(objects, lodashStable.isEqual);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[1]
        ]);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
            return isEven(index) ? -0 : __num_top__;
        });
        var arrays = [
                [
                    -__num_top__,
                    0
                ],
                largeArray
            ], expected = lodashStable.map(arrays, lodashStable.constant(['-0']));
        var actual = lodashStable.map(arrays, function (array) {
            return lodashStable.map(_.uniqWith(array, lodashStable.eq), lodashStable.toString);
        });
        assert.deepEqual(actual, expected);
    });
}());