QUnit.module('lodash.propertyOf');
(function () {
    QUnit.test('should create a function that plucks a property value of a given key', function (assert) {
        assert.expect(3);
        var object = { 'a': 1 }, propOf = _.propertyOf(object);
        assert.strictEqual(propOf.length, 1);
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            assert.strictEqual(propOf(path), 1);
        });
    });
    QUnit.test('should pluck deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': 2 } }, propOf = _.propertyOf(object);
        lodashStable.each([
            'a.b',
            [
                'a',
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(propOf(path), 2);
        });
    });
    QUnit.test('should pluck inherited property values', function (assert) {
        assert.expect(2);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var propOf = _.propertyOf(new Foo());
        lodashStable.each([
            'b',
            ['b']
        ], function (path) {
            assert.strictEqual(propOf(path), 2);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3
            ], propOf = _.propertyOf(array);
        lodashStable.each([
            1,
            [1]
        ], function (path) {
            assert.strictEqual(propOf(path), 2);
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
            var propOf = _.propertyOf(object);
            return propOf(key);
        });
        assert.deepEqual(actual, [
            'a',
            'a',
            'b',
            'b'
        ]);
    });
    QUnit.test('should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant('fn');
        var expected = [
                1,
                2,
                3,
                4
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
                var propOf = _.propertyOf(object);
                return propOf(index ? [path] : path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should pluck a key over a path', function (assert) {
        assert.expect(2);
        var object = {
                'a.b': 1,
                'a': { 'b': 2 }
            }, propOf = _.propertyOf(object);
        lodashStable.each([
            'a.b',
            ['a.b']
        ], function (path) {
            assert.strictEqual(propOf(path), 1);
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
            var actual = lodashStable.map(values, function (value, index) {
                var propOf = index ? _.propertyOf(value) : _.propertyOf();
                return propOf(path);
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
            var actual = lodashStable.map(values, function (value, index) {
                var propOf = index ? _.propertyOf(value) : _.propertyOf();
                return propOf(path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should return `undefined` if parts of `path` are missing', function (assert) {
        assert.expect(4);
        var propOf = _.propertyOf({});
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
            assert.strictEqual(propOf(path), undefined);
        });
    });
}());