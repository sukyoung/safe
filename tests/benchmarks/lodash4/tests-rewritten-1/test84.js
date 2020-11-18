QUnit.module('has methods');
lodashStable.each([
    'has',
    'hasIn'
], function (methodName) {
    var func = _[methodName], isHas = methodName == 'has', sparseArgs = toArgs([1]), sparseArray = Array(1), sparseString = Object('a');
    delete sparseArgs[0];
    delete sparseString[0];
    QUnit.test('`_.' + methodName + '` should check for own properties', function (assert) {
        assert.expect(2);
        var object = { 'a': 1 };
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            assert.strictEqual(func(object, path), true);
        });
    });
    QUnit.test('`_.' + methodName + '` should not use the `hasOwnProperty` method of `object`', function (assert) {
        assert.expect(1);
        var object = {
            'hasOwnProperty': null,
            'a': 1
        };
        assert.strictEqual(func(object, 'a'), true);
    });
    QUnit.test('`_.' + methodName + '` should support deep paths', function (assert) {
        assert.expect(4);
        var object = { 'a': { 'b': 2 } };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), true);
        });
        lodashStable.each([
            'a.a',
            [
                'a',
                'a'
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), false);
        });
    });
    QUnit.test('`_.' + methodName + '` should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant('fn');
        var object = {
                'null': 1,
                'undefined': 2,
                'fn': 3,
                '[object Object]': 4
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ], expected = lodashStable.map(paths, stubTrue);
        lodashStable.times(2, function (index) {
            var actual = lodashStable.map(paths, function (path) {
                return func(object, index ? [path] : path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should work with `arguments` objects', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(args, 1), true);
    });
    QUnit.test('`_.' + methodName + '` should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = [
            1,
            2,
            3
        ];
        lodashStable.each([
            1,
            [1]
        ], function (path) {
            assert.strictEqual(func(array, path), true);
        });
    });
    QUnit.test('`_.' + methodName + '` should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object = {
                '-0': 'a',
                '0': 'b'
            }, props = [
                -0,
                Object(-0),
                0,
                Object(0)
            ], expected = lodashStable.map(props, stubTrue);
        var actual = lodashStable.map(props, function (key) {
            return func(object, key);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        function Foo() {
        }
        if (Symbol) {
            Foo.prototype[symbol] = 1;
            var symbol2 = Symbol('b');
            defineProperty(Foo.prototype, symbol2, {
                'configurable': true,
                'enumerable': false,
                'writable': true,
                'value': 2
            });
            var object = isHas ? Foo.prototype : new Foo();
            assert.strictEqual(func(object, symbol), true);
            assert.strictEqual(func(object, symbol2), true);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('`_.' + methodName + '` should check for a key over a path', function (assert) {
        assert.expect(2);
        var object = { 'a.b': 1 };
        lodashStable.each([
            'a.b',
            ['a.b']
        ], function (path) {
            assert.strictEqual(func(object, path), true);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `true` for indexes of sparse values', function (assert) {
        assert.expect(1);
        var values = [
                sparseArgs,
                sparseArray,
                sparseString
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return func(value, 0);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `true` for indexes of sparse values with deep paths', function (assert) {
        assert.expect(1);
        var values = [
                sparseArgs,
                sparseArray,
                sparseString
            ], expected = lodashStable.map(values, lodashStable.constant([
                true,
                true
            ]));
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.map([
                'a[0]',
                [
                    'a',
                    '0'
                ]
            ], function (path) {
                return func({ 'a': value }, path);
            });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `' + (isHas ? 'false' : 'true') + '` for inherited properties', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = 1;
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            assert.strictEqual(func(new Foo(), path), !isHas);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `' + (isHas ? 'false' : 'true') + '` for nested inherited properties', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = { 'b': 1 };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            assert.strictEqual(func(new Foo(), path), !isHas);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `false` when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            'constructor',
            ['constructor']
        ], function (path) {
            var actual = lodashStable.map(values, function (value) {
                return func(value, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `false` for deep paths when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            'constructor.prototype.valueOf',
            [
                'constructor',
                'prototype',
                'valueOf'
            ]
        ], function (path) {
            var actual = lodashStable.map(values, function (value) {
                return func(value, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `false` for nullish values of nested objects', function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var actual = lodashStable.map(values, function (value, index) {
                var object = index ? { 'a': value } : {};
                return func(object, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `false` over sparse values of deep paths', function (assert) {
        assert.expect(1);
        var values = [
                sparseArgs,
                sparseArray,
                sparseString
            ], expected = lodashStable.map(values, lodashStable.constant([
                false,
                false
            ]));
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.map([
                'a[0].b',
                [
                    'a',
                    '0',
                    'b'
                ]
            ], function (path) {
                return func({ 'a': value }, path);
            });
        });
        assert.deepEqual(actual, expected);
    });
});