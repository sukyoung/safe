QUnit.module('lodash.uniqWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': 1,
                    'y': __num_top__
                },
                {
                    'x': 2,
                    'y': 1
                },
                {
                    'x': 1,
                    'y': 2
                }
            ], actual = _.uniqWith(objects, lodashStable.isEqual);
        assert.deepEqual(actual, [
            objects[0],
            objects[1]
        ]);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
            return isEven(index) ? -0 : 0;
        });
        var arrays = [
                [
                    -0,
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