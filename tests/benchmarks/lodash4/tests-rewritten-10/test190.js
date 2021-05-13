QUnit.module('lodash.property');
(function () {
    QUnit.test('should create a function that plucks a property value of a given object', function (assert) {
        assert.expect(4);
        var object = { 'a': 1 };
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop.length, 1);
            assert.strictEqual(prop(object), 1);
        });
    });
    QUnit.test('should pluck deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': 2 } };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(object), 2);
        });
    });
    QUnit.test('should pluck inherited property values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = 1;
        lodashStable.each([
            __str_top__,
            ['a']
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(new Foo()), 1);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        lodashStable.each([
            1,
            [__num_top__]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(array), __num_top__);
        });
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object = {
                '-0': 'a',
                '0': 'b'
            }, props = [
                -0,
                Object(-0),
                0,
                Object(0)
            ];
        var actual = lodashStable.map(props, function (key) {
            var prop = _.property(key);
            return prop(object);
        });
        assert.deepEqual(actual, [
            'a',
            'a',
            __str_top__,
            'b'
        ]);
    });
    QUnit.test('should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant(__str_top__);
        var expected = [
                1,
                2,
                3,
                __num_top__
            ], object = {
                'null': 1,
                'undefined': 2,
                'fn': 3,
                '[object Object]': 4
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ];
        lodashStable.times(2, function (index) {
            var actual = lodashStable.map(paths, function (path) {
                var prop = _.property(index ? [path] : path);
                return prop(object);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should pluck a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': 1,
            'a': { 'b': 2 }
        };
        lodashStable.each([
            __str_top__,
            ['a.b']
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(object), 1);
        });
    });
    QUnit.test('should return `undefined` when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, noop);
        lodashStable.each([
            'constructor',
            ['constructor']
        ], function (path) {
            var prop = _.property(path);
            var actual = lodashStable.map(values, function (value, index) {
                return index ? prop(value) : prop();
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should return `undefined` for deep paths when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, noop);
        lodashStable.each([
            'constructor.prototype.valueOf',
            [
                'constructor',
                'prototype',
                'valueOf'
            ]
        ], function (path) {
            var prop = _.property(path);
            var actual = lodashStable.map(values, function (value, index) {
                return index ? prop(value) : prop();
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should return `undefined` if parts of `path` are missing', function (assert) {
        assert.expect(4);
        var object = {};
        lodashStable.each([
            'a',
            'a[1].b.c',
            ['a'],
            [
                'a',
                '1',
                'b',
                'c'
            ]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(object), undefined);
        });
    });
}());