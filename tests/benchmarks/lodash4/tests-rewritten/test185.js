QUnit.module('methods using `createWrapper`');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    var ph1 = _.bind.placeholder, ph2 = _.bindKey.placeholder, ph3 = _.partial.placeholder, ph4 = _.partialRight.placeholder;
    QUnit.test('should work with combinations of partial functions', function (assert) {
        assert.expect(1);
        var a = _.partial(fn), b = _.partialRight(a, __num_top__), c = _.partial(b, __num_top__);
        assert.deepEqual(c(__num_top__), [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with combinations of bound and partial functions', function (assert) {
        assert.expect(3);
        var fn = function () {
            var result = [this.a];
            push.apply(result, arguments);
            return result;
        };
        var expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], object = {
                'a': __num_top__,
                'fn': fn
            };
        var a = _.bindKey(object, __str_top__), b = _.partialRight(a, __num_top__), c = _.partial(b, __num_top__);
        assert.deepEqual(c(__num_top__), expected);
        a = _.bind(fn, object);
        b = _.partialRight(a, __num_top__);
        c = _.partial(b, __num_top__);
        assert.deepEqual(c(__num_top__), expected);
        a = _.partial(fn, __num_top__);
        b = _.bind(a, object);
        c = _.partialRight(b, __num_top__);
        assert.deepEqual(c(__num_top__), expected);
    });
    QUnit.test('should ensure `new combo` is an instance of `func`', function (assert) {
        assert.expect(2);
        function Foo(a, b, c) {
            return b === __num_top__ && object;
        }
        var combo = _.partial(_.partialRight(Foo, __num_top__), __num_top__), object = {};
        assert.ok(new combo(__num_top__) instanceof Foo);
        assert.strictEqual(new combo(__num_top__), object);
    });
    QUnit.test('should work with combinations of functions with placeholders', function (assert) {
        assert.expect(3);
        var expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], object = { 'fn': fn };
        var a = _.bindKey(object, __str_top__, ph2, __num_top__), b = _.partialRight(a, ph4, __num_top__), c = _.partial(b, __num_top__, ph3, __num_top__);
        assert.deepEqual(c(__num_top__, __num_top__), expected);
        a = _.bind(fn, object, ph1, __num_top__);
        b = _.partialRight(a, ph4, __num_top__);
        c = _.partial(b, __num_top__, ph3, __num_top__);
        assert.deepEqual(c(__num_top__, __num_top__), expected);
        a = _.partial(fn, ph3, __num_top__);
        b = _.bind(a, object, __num_top__, ph1, __num_top__);
        c = _.partialRight(b, ph4, __num_top__);
        assert.deepEqual(c(__num_top__, __num_top__), expected);
    });
    QUnit.test('should work with combinations of functions with overlapping placeholders', function (assert) {
        assert.expect(3);
        var expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], object = { 'fn': fn };
        var a = _.bindKey(object, __str_top__, ph2, __num_top__), b = _.partialRight(a, ph4, __num_top__), c = _.partial(b, ph3, __num_top__);
        assert.deepEqual(c(__num_top__), expected);
        a = _.bind(fn, object, ph1, __num_top__);
        b = _.partialRight(a, ph4, __num_top__);
        c = _.partial(b, ph3, __num_top__);
        assert.deepEqual(c(__num_top__), expected);
        a = _.partial(fn, ph3, __num_top__);
        b = _.bind(a, object, ph1, __num_top__);
        c = _.partialRight(b, ph4, __num_top__);
        assert.deepEqual(c(__num_top__), expected);
    });
    QUnit.test('should work with recursively bound functions', function (assert) {
        assert.expect(1);
        var fn = function () {
            return this.a;
        };
        var a = _.bind(fn, { 'a': __num_top__ }), b = _.bind(a, { 'a': __num_top__ }), c = _.bind(b, { 'a': __num_top__ });
        assert.strictEqual(c(), __num_top__);
    });
    QUnit.test('should work when hot', function (assert) {
        assert.expect(12);
        lodashStable.times(__num_top__, function (index) {
            var fn = function () {
                var result = [this];
                push.apply(result, arguments);
                return result;
            };
            var object = {}, bound1 = index ? _.bind(fn, object, __num_top__) : _.bind(fn, object), expected = [
                    object,
                    __num_top__,
                    __num_top__,
                    __num_top__
                ];
            var actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var bound2 = index ? _.bind(bound1, null, __num_top__) : _.bind(bound1);
                return index ? bound2(__num_top__) : bound2(__num_top__, __num_top__, __num_top__);
            }));
            assert.deepEqual(actual, expected);
            actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var bound1 = index ? _.bind(fn, object, __num_top__) : _.bind(fn, object), bound2 = index ? _.bind(bound1, null, __num_top__) : _.bind(bound1);
                return index ? bound2(__num_top__) : bound2(__num_top__, __num_top__, __num_top__);
            }));
            assert.deepEqual(actual, expected);
        });
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (methodName, index) {
            var fn = function (a, b, c) {
                    return [
                        a,
                        b,
                        c
                    ];
                }, curried = _[methodName](fn), expected = index ? [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ] : [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ];
            var actual = _.last(lodashStable.times(HOT_COUNT, function () {
                return curried(__num_top__)(__num_top__)(__num_top__);
            }));
            assert.deepEqual(actual, expected);
            actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var curried = _[methodName](fn);
                return curried(__num_top__)(__num_top__)(__num_top__);
            }));
            assert.deepEqual(actual, expected);
        });
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (methodName, index) {
            var func = _[methodName], fn = function () {
                    return slice.call(arguments);
                }, par1 = func(fn, __num_top__), expected = index ? [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ] : [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ];
            var actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var par2 = func(par1, __num_top__);
                return par2(__num_top__);
            }));
            assert.deepEqual(actual, expected);
            actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var par1 = func(fn, __num_top__), par2 = func(par1, __num_top__);
                return par2(__num_top__);
            }));
            assert.deepEqual(actual, expected);
        });
    });
}());