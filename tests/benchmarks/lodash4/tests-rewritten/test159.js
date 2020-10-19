QUnit.module('lodash.methodOf');
(function () {
    QUnit.test('should create a function that calls a method of a given key', function (assert) {
        assert.expect(4);
        var object = { 'a': stubOne };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var methodOf = _.methodOf(object);
            assert.strictEqual(methodOf.length, __num_top__);
            assert.strictEqual(methodOf(path), __num_top__);
        });
    });
    QUnit.test('should work with deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': stubTwo } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var methodOf = _.methodOf(object);
            assert.strictEqual(methodOf(path), __num_top__);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = lodashStable.times(__num_top__, _.constant);
        lodashStable.each([
            __num_top__,
            [__num_top__]
        ], function (path) {
            var methodOf = _.methodOf(array);
            assert.strictEqual(methodOf(path), __num_top__);
        });
    });
    QUnit.test('should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant(__str_top__);
        var expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
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
        lodashStable.times(__num_top__, function (index) {
            var actual = lodashStable.map(paths, function (path) {
                var methodOf = _.methodOf(object);
                return methodOf(index ? [path] : path);
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
            __str_top__,
            [__str_top__]
        ], function (path) {
            var methodOf = _.methodOf(new Foo());
            assert.strictEqual(methodOf(path), __num_top__);
        });
    });
    QUnit.test('should use a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': stubOne,
            'a': { 'b': stubTwo }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var methodOf = _.methodOf(object);
            assert.strictEqual(methodOf(path), __num_top__);
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
            __str_top__,
            [__str_top__]
        ], function (path) {
            var actual = lodashStable.map(values, function (value, index) {
                var methodOf = index ? _.methodOf() : _.methodOf(value);
                return methodOf(path);
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
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var actual = lodashStable.map(values, function (value, index) {
                var methodOf = index ? _.methodOf() : _.methodOf(value);
                return methodOf(path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should return `undefined` if parts of `path` are missing', function (assert) {
        assert.expect(4);
        var object = {}, methodOf = _.methodOf(object);
        lodashStable.each([
            __str_top__,
            __str_top__,
            [__str_top__],
            [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(methodOf(path), undefined);
        });
    });
    QUnit.test('should apply partial arguments to function', function (assert) {
        assert.expect(2);
        var object = {
            'fn': function () {
                return slice.call(arguments);
            }
        };
        var methodOf = _.methodOf(object, __num_top__, __num_top__, __num_top__);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.deepEqual(methodOf(path), [
                __num_top__,
                __num_top__,
                __num_top__
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
                    'c': __num_top__
                }
            }, methodOf = _.methodOf(object);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(methodOf(path), __num_top__);
        });
    });
}());