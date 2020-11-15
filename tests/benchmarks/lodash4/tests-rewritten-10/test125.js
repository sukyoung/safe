QUnit.module('lodash.isString');
(function () {
    QUnit.test('should return `true` for strings', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isString('a'), true);
        assert.strictEqual(_.isString(Object('a')), __bool_top__);
    });
    QUnit.test('should return `false` for non-strings', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, function (value) {
            return value === __str_top__;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isString(value) : _.isString();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isString(args), __bool_top__);
        assert.strictEqual(_.isString([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(_.isString(true), __bool_top__);
        assert.strictEqual(_.isString(new Date()), false);
        assert.strictEqual(_.isString(new Error()), __bool_top__);
        assert.strictEqual(_.isString(_), __bool_top__);
        assert.strictEqual(_.isString(slice), false);
        assert.strictEqual(_.isString({
            '0': 1,
            'length': 1
        }), false);
        assert.strictEqual(_.isString(1), __bool_top__);
        assert.strictEqual(_.isString(/x/), __bool_top__);
        assert.strictEqual(_.isString(symbol), false);
    });
    QUnit.test('should work with strings from another realm', function (assert) {
        assert.expect(1);
        if (realm.string) {
            assert.strictEqual(_.isString(realm.string), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());