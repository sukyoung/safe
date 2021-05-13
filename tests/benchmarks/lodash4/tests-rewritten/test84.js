QUnit.module('has methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isHas = methodName == __str_top__, sparseArgs = toArgs([__num_top__]), sparseArray = Array(__num_top__), sparseString = Object(__str_top__);
    delete sparseArgs[__num_top__];
    delete sparseString[__num_top__];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': __num_top__ };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(func(object, path), __bool_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
            'hasOwnProperty': null,
            'a': __num_top__
        };
        assert.strictEqual(func(object, __str_top__), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        var object = { 'a': { 'b': __num_top__ } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), __bool_top__);
        });
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), __bool_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant(__str_top__);
        var object = {
                'null': __num_top__,
                'undefined': __num_top__,
                'fn': __num_top__,
                '[object Object]': __num_top__
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ], expected = lodashStable.map(paths, stubTrue);
        lodashStable.times(__num_top__, function (index) {
            var actual = lodashStable.map(paths, function (path) {
                return func(object, index ? [path] : path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(args, __num_top__), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        lodashStable.each([
            __num_top__,
            [__num_top__]
        ], function (path) {
            assert.strictEqual(func(array, path), __bool_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '-0': __str_top__,
                '0': __str_top__
            }, props = [
                -__num_top__,
                Object(-__num_top__),
                __num_top__,
                Object(__num_top__)
            ], expected = lodashStable.map(props, stubTrue);
        var actual = lodashStable.map(props, function (key) {
            return func(object, key);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        function Foo() {
        }
        if (Symbol) {
            Foo.prototype[symbol] = __num_top__;
            var symbol2 = Symbol(__str_top__);
            defineProperty(Foo.prototype, symbol2, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'writable': __bool_top__,
                'value': __num_top__
            });
            var object = isHas ? Foo.prototype : new Foo();
            assert.strictEqual(func(object, symbol), __bool_top__);
            assert.strictEqual(func(object, symbol2), __bool_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a.b': __num_top__ };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(func(object, path), __bool_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                sparseArgs,
                sparseArray,
                sparseString
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return func(value, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                sparseArgs,
                sparseArray,
                sparseString
            ], expected = lodashStable.map(values, lodashStable.constant([
                __bool_top__,
                __bool_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.map([
                __str_top__,
                [
                    __str_top__,
                    __str_top__
                ]
            ], function (path) {
                return func({ 'a': value }, path);
            });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isHas ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = __num_top__;
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(func(new Foo(), path), !isHas);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isHas ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = { 'b': __num_top__ };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(func(new Foo(), path), !isHas);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var actual = lodashStable.map(values, function (value) {
                return func(value, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var actual = lodashStable.map(values, function (value) {
                return func(value, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var actual = lodashStable.map(values, function (value, index) {
                var object = index ? { 'a': value } : {};
                return func(object, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                sparseArgs,
                sparseArray,
                sparseString
            ], expected = lodashStable.map(values, lodashStable.constant([
                __bool_top__,
                __bool_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.map([
                __str_top__,
                [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ]
            ], function (path) {
                return func({ 'a': value }, path);
            });
        });
        assert.deepEqual(actual, expected);
    });
});