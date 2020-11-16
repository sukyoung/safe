QUnit.module('lodash.method');
(function () {
    QUnit.test('should create a function that calls a method of a given object', function (assert) {
        assert.expect(4);
        var object = { 'a': stubOne };
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            var method = _.method(path);
            assert.strictEqual(method.length, 1);
            assert.strictEqual(method(object), 1);
        });
    });
    QUnit.test('should work with deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': stubTwo } };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var method = _.method(path);
            assert.strictEqual(method(object), 2);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = lodashStable.times(3, _.constant);
        lodashStable.each([
            1,
            [1]
        ], function (path) {
            var method = _.method(path);
            assert.strictEqual(method(array), 1);
        });
    });
    QUnit.test('should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant('fn');
        var expected = [
                1,
                __num_top__,
                3,
                4
            ], object = {
                'null': stubOne,
                'undefined': stubTwo,
                'fn': stubThree,
                '[object Object]': stubFour
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ];
        lodashStable.times(2, function (index) {
            var actual = lodashStable.map(paths, function (path) {
                var method = _.method(index ? [path] : path);
                return method(object);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should work with inherited property values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = stubOne;
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            var method = _.method(path);
            assert.strictEqual(method(new Foo()), 1);
        });
    });
    QUnit.test('should use a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': stubOne,
            'a': { 'b': stubTwo }
        };
        lodashStable.each([
            'a.b',
            ['a.b']
        ], function (path) {
            var method = _.method(path);
            assert.strictEqual(method(object), 1);
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
            var method = _.method(path);
            var actual = lodashStable.map(values, function (value, index) {
                return index ? method(value) : method();
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
            var method = _.method(path);
            var actual = lodashStable.map(values, function (value, index) {
                return index ? method(value) : method();
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
            var method = _.method(path);
            assert.strictEqual(method(object), undefined);
        });
    });
    QUnit.test('should apply partial arguments to function', function (assert) {
        assert.expect(2);
        var object = {
            'fn': function () {
                return slice.call(arguments);
            }
        };
        lodashStable.each([
            'fn',
            ['fn']
        ], function (path) {
            var method = _.method(path, 1, 2, 3);
            assert.deepEqual(method(object), [
                1,
                2,
                3
            ]);
        });
    });
    QUnit.test('should invoke deep property methods with the correct `this` binding', function (assert) {
        assert.expect(2);
        var object = {
            'a': {
                'b': function () {
                    return this.c;
                },
                'c': 1
            }
        };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var method = _.method(path);
            assert.strictEqual(method(object), 1);
        });
    });
}());