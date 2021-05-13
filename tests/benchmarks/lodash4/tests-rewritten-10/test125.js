QUnit.module('lodash.isString');
(function () {
    QUnit.test('should return `true` for strings', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.isString('a'), __bool_top__);
        assert.strictEqual(_.isString(Object('a')), true);
    });
    QUnit.test('should return `false` for non-strings', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, function (value) {
            return value === '';
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isString(value) : _.isString();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isString(args), false);
        assert.strictEqual(_.isString([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isString(true), false);
        assert.strictEqual(_.isString(new Date()), false);
        assert.strictEqual(_.isString(new Error()), false);
        assert.strictEqual(_.isString(_), false);
        assert.strictEqual(_.isString(slice), false);
        assert.strictEqual(_.isString({
            '0': __num_top__,
            'length': __num_top__
        }), __bool_top__);
        assert.strictEqual(_.isString(1), __bool_top__);
        assert.strictEqual(_.isString(/x/), __bool_top__);
        assert.strictEqual(_.isString(symbol), false);
    });
    QUnit.test('should work with strings from another realm', function (assert) {
        assert.expect(1);
        if (realm.string) {
            assert.strictEqual(_.isString(realm.string), true);
        } else {
            skipAssert(assert);
        }
    });
}());