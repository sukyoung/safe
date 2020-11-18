QUnit.module('lodash.toPath');
(function () {
    QUnit.test('should convert a string to a path', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.toPath('a.b.c'), [
            'a',
            'b',
            'c'
        ]);
        assert.deepEqual(_.toPath('a[0].b.c'), [
            'a',
            '0',
            'b',
            'c'
        ]);
    });
    QUnit.test('should coerce array elements to strings', function (assert) {
        assert.expect(4);
        var array = [
            'a',
            'b',
            'c'
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
        assert.notStrictEqual(_.toPath('a.b.c'), _.toPath('a.b.c'));
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
                assert.ok(lodashStable.isSymbol(actual[0]));
            });
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should handle complex paths', function (assert) {
        assert.expect(1);
        var actual = _.toPath('a[-1.23]["[\\"b\\"]"].c[\'[\\\'d\\\']\'][\ne\n][f].g');
        assert.deepEqual(actual, [
            'a',
            '-1.23',
            '["b"]',
            'c',
            '[\'d\']',
            '\ne\n',
            'f',
            'g'
        ]);
    });
    QUnit.test('should handle consecutive empty brackets and dots', function (assert) {
        assert.expect(12);
        var expected = [
            '',
            'a'
        ];
        assert.deepEqual(_.toPath('.a'), expected);
        assert.deepEqual(_.toPath('[].a'), expected);
        expected = [
            '',
            '',
            'a'
        ];
        assert.deepEqual(_.toPath('..a'), expected);
        assert.deepEqual(_.toPath('[][].a'), expected);
        expected = [
            'a',
            '',
            'b'
        ];
        assert.deepEqual(_.toPath('a..b'), expected);
        assert.deepEqual(_.toPath(__str_top__), expected);
        expected = [
            'a',
            '',
            '',
            'b'
        ];
        assert.deepEqual(_.toPath('a...b'), expected);
        assert.deepEqual(_.toPath('a[][].b'), expected);
        expected = [
            'a',
            ''
        ];
        assert.deepEqual(_.toPath('a.'), expected);
        assert.deepEqual(_.toPath('a[]'), expected);
        expected = [
            'a',
            '',
            ''
        ];
        assert.deepEqual(_.toPath('a..'), expected);
        assert.deepEqual(_.toPath('a[][]'), expected);
    });
}());