QUnit.module('lodash.property');
(function () {
    QUnit.test('should create a function that plucks a property value of a given object', function (assert) {
        assert.expect(4);
        var object = { 'a': __num_top__ };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop.length, __num_top__);
            assert.strictEqual(prop(object), __num_top__);
        });
    });
    QUnit.test('should pluck deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': __num_top__ } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(object), __num_top__);
        });
    });
    QUnit.test('should pluck inherited property values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.a = __num_top__;
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(new Foo()), __num_top__);
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
            __num_top__,
            [__num_top__]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(array), __num_top__);
        });
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object = {
                '-0': __str_top__,
                '0': __str_top__
            }, props = [
                -__num_top__,
                Object(-__num_top__),
                __num_top__,
                Object(__num_top__)
            ];
        var actual = lodashStable.map(props, function (key) {
            var prop = _.property(key);
            return prop(object);
        });
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
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
                'null': __num_top__,
                'undefined': __num_top__,
                'fn': __num_top__,
                '[object Object]': __num_top__
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ];
        lodashStable.times(__num_top__, function (index) {
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
            'a.b': __num_top__,
            'a': { 'b': __num_top__ }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var prop = _.property(path);
            assert.strictEqual(prop(object), __num_top__);
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
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
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
            var prop = _.property(path);
            assert.strictEqual(prop(object), undefined);
        });
    });
}());