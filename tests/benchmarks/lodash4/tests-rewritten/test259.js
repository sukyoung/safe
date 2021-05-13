QUnit.module('lodash.toPath');
(function () {
    QUnit.test('should convert a string to a path', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.toPath(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.toPath(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should coerce array elements to strings', function (assert) {
        assert.expect(4);
        var array = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        lodashStable.each([
            array,
            lodashStable.map(array, Object)
        ], function (value) {
            var actual = _.toPath(value);
            assert.deepEqual(actual, array);
            assert.notStrictEqual(actual, array);
        });
    });
    QUnit.test('should return new path array', function (assert) {
        assert.expect(1);
        assert.notStrictEqual(_.toPath(__str_top__), _.toPath(__str_top__));
    });
    QUnit.test('should not coerce symbols to strings', function (assert) {
        assert.expect(4);
        if (Symbol) {
            var object = Object(symbol);
            lodashStable.each([
                symbol,
                object,
                [symbol],
                [object]
            ], function (value) {
                var actual = _.toPath(value);
                assert.ok(lodashStable.isSymbol(actual[__num_top__]));
            });
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should handle complex paths', function (assert) {
        assert.expect(1);
        var actual = _.toPath(__str_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should handle consecutive empty brackets and dots', function (assert) {
        assert.expect(12);
        var expected = [
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.toPath(__str_top__), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
        expected = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.toPath(__str_top__), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
        expected = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.toPath(__str_top__), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
        expected = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.toPath(__str_top__), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
        expected = [
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.toPath(__str_top__), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
        expected = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.toPath(__str_top__), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
    });
}());