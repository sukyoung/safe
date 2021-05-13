QUnit.module('lodash.differenceWith');
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
                    'y': 1
                }
            ], actual = _.differenceWith(objects, [{
                    'x': __num_top__,
                    'y': __num_top__
                }], lodashStable.isEqual);
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var array = [
                -__num_top__,
                __num_top__
            ], largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubOne), others = [
                [__num_top__],
                largeArray
            ], expected = lodashStable.map(others, lodashStable.constant([__str_top__]));
        var actual = lodashStable.map(others, function (other) {
            return lodashStable.map(_.differenceWith(array, other, lodashStable.eq), lodashStable.toString);
        });
        assert.deepEqual(actual, expected);
    });
}());