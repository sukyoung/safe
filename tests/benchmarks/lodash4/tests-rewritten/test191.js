QUnit.module('lodash.propertyOf');
(function () {
    QUnit.test('should create a function that plucks a property value of a given key', function (assert) {
        assert.expect(3);
        var object = { 'a': __num_top__ }, propOf = _.propertyOf(object);
        assert.strictEqual(propOf.length, __num_top__);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(propOf(path), __num_top__);
        });
    });
    QUnit.test('should pluck deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': __num_top__ } }, propOf = _.propertyOf(object);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(propOf(path), __num_top__);
        });
    });
    QUnit.test('should pluck inherited property values', function (assert) {
        assert.expect(2);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var propOf = _.propertyOf(new Foo());
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(propOf(path), __num_top__);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], propOf = _.propertyOf(array);
        lodashStable.each([
            __num_top__,
            [__num_top__]
        ], function (path) {
            assert.strictEqual(propOf(path), __num_top__);
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
            var propOf = _.propertyOf(object);
            return propOf(key);
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
                var propOf = _.propertyOf(object);
                return propOf(index ? [path] : path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should pluck a key over a path', function (assert) {
        assert.expect(2);
        var object = {
                'a.b': __num_top__,
                'a': { 'b': __num_top__ }
            }, propOf = _.propertyOf(object);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(propOf(path), __num_top__);
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
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
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
            assert.strictEqual(propOf(path), undefined);
        });
    });
}());