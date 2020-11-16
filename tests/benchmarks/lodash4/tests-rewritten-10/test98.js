QUnit.module('lodash.isArray');
(function () {
    QUnit.test('should return `true` for arrays', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isArray([
            1,
            2,
            3
        ]), true);
    });
    QUnit.test('should return `false` for non-arrays', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isArray(value) : _.isArray();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isArray(args), __bool_top__);
        assert.strictEqual(_.isArray(true), __bool_top__);
        assert.strictEqual(_.isArray(new Date()), false);
        assert.strictEqual(_.isArray(new Error()), __bool_top__);
        assert.strictEqual(_.isArray(_), __bool_top__);
        assert.strictEqual(_.isArray(slice), __bool_top__);
        assert.strictEqual(_.isArray({
            '0': 1,
            'length': 1
        }), __bool_top__);
        assert.strictEqual(_.isArray(1), false);
        assert.strictEqual(_.isArray(/x/), false);
        assert.strictEqual(_.isArray(__str_top__), __bool_top__);
        assert.strictEqual(_.isArray(symbol), __bool_top__);
    });
    QUnit.test('should work with an array from another realm', function (assert) {
        assert.expect(1);
        if (realm.array) {
            assert.strictEqual(_.isArray(realm.array), true);
        } else {
            skipAssert(assert);
        }
    });
}());