QUnit.module('lodash.toString');
(function () {
    QUnit.test('should treat nullish values as empty strings', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubString);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.toString(value) : _.toString();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var values = [
                -__num_top__,
                Object(-0),
                0,
                Object(0)
            ], expected = [
                __str_top__,
                __str_top__,
                '0',
                __str_top__
            ], actual = lodashStable.map(values, _.toString);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should preserve the sign of `0` in an array', function (assert) {
        assert.expect(1);
        var values = [
            -0,
            Object(-__num_top__),
            __num_top__,
            Object(0)
        ];
        assert.deepEqual(_.toString(values), '-0,-0,0,0');
    });
    QUnit.test('should not error on symbols', function (assert) {
        assert.expect(1);
        if (Symbol) {
            try {
                assert.strictEqual(_.toString(symbol), 'Symbol(a)');
            } catch (e) {
                assert.ok(false, e.message);
            }
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should not error on an array of symbols', function (assert) {
        assert.expect(1);
        if (Symbol) {
            try {
                assert.strictEqual(_.toString([symbol]), 'Symbol(a)');
            } catch (e) {
                assert.ok(false, e.message);
            }
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return the `toString` result of the wrapped value', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _([
                __num_top__,
                __num_top__,
                __num_top__
            ]);
            assert.strictEqual(wrapped.toString(), '1,2,3');
        } else {
            skipAssert(assert);
        }
    });
}());