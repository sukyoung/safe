QUnit.module('lodash.isSymbol');
(function () {
    QUnit.test('should return `true` for symbols', function (assert) {
        assert.expect(2);
        if (Symbol) {
            assert.strictEqual(_.isSymbol(symbol), __bool_top__);
            assert.strictEqual(_.isSymbol(Object(symbol)), __bool_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should return `false` for non-symbols', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isSymbol(value) : _.isSymbol();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isSymbol(args), __bool_top__);
        assert.strictEqual(_.isSymbol([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isSymbol(__bool_top__), __bool_top__);
        assert.strictEqual(_.isSymbol(new Date()), __bool_top__);
        assert.strictEqual(_.isSymbol(new Error()), __bool_top__);
        assert.strictEqual(_.isSymbol(_), __bool_top__);
        assert.strictEqual(_.isSymbol(slice), __bool_top__);
        assert.strictEqual(_.isSymbol({
            '0': __num_top__,
            'length': __num_top__
        }), __bool_top__);
        assert.strictEqual(_.isSymbol(__num_top__), __bool_top__);
        assert.strictEqual(_.isSymbol(/x/), __bool_top__);
        assert.strictEqual(_.isSymbol(__str_top__), __bool_top__);
    });
    QUnit.test('should work with symbols from another realm', function (assert) {
        assert.expect(1);
        if (Symbol && realm.symbol) {
            assert.strictEqual(_.isSymbol(realm.symbol), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());