QUnit.module('lodash.isSymbol');
(function () {
    QUnit.test('should return `true` for symbols', function (assert) {
        assert.expect(2);
        if (Symbol) {
            assert.strictEqual(_.isSymbol(symbol), true);
            assert.strictEqual(_.isSymbol(Object(symbol)), true);
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
        assert.strictEqual(_.isSymbol(args), false);
        assert.strictEqual(_.isSymbol([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(_.isSymbol(true), false);
        assert.strictEqual(_.isSymbol(new Date()), false);
        assert.strictEqual(_.isSymbol(new Error()), false);
        assert.strictEqual(_.isSymbol(_), __bool_top__);
        assert.strictEqual(_.isSymbol(slice), false);
        assert.strictEqual(_.isSymbol({
            '0': 1,
            'length': 1
        }), false);
        assert.strictEqual(_.isSymbol(1), false);
        assert.strictEqual(_.isSymbol(/x/), false);
        assert.strictEqual(_.isSymbol('a'), false);
    });
    QUnit.test('should work with symbols from another realm', function (assert) {
        assert.expect(1);
        if (Symbol && realm.symbol) {
            assert.strictEqual(_.isSymbol(realm.symbol), true);
        } else {
            skipAssert(assert);
        }
    });
}());