QUnit.module('lodash.isArrayLike');
(function () {
    QUnit.test('should return `true` for array-like values', function (assert) {
        assert.expect(1);
        var values = [
                args,
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ],
                {
                    '0': __str_top__,
                    'length': __num_top__
                },
                __str_top__
            ], expected = lodashStable.map(values, stubTrue), actual = lodashStable.map(values, _.isArrayLike);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-arrays', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, function (value) {
            return value === __str_top__;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isArrayLike(value) : _.isArrayLike();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isArrayLike(__bool_top__), __bool_top__);
        assert.strictEqual(_.isArrayLike(new Date()), __bool_top__);
        assert.strictEqual(_.isArrayLike(new Error()), __bool_top__);
        assert.strictEqual(_.isArrayLike(_), __bool_top__);
        assert.strictEqual(_.isArrayLike(asyncFunc), __bool_top__);
        assert.strictEqual(_.isArrayLike(genFunc), __bool_top__);
        assert.strictEqual(_.isArrayLike(slice), __bool_top__);
        assert.strictEqual(_.isArrayLike({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isArrayLike(__num_top__), __bool_top__);
        assert.strictEqual(_.isArrayLike(/x/), __bool_top__);
        assert.strictEqual(_.isArrayLike(symbol), __bool_top__);
    });
    QUnit.test('should work with an array from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            var values = [
                    realm.arguments,
                    realm.array,
                    realm.string
                ], expected = lodashStable.map(values, stubTrue), actual = lodashStable.map(values, _.isArrayLike);
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());