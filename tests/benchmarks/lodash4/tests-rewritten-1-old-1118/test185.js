QUnit.module('methods using `createWrapper`');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    var ph1 = _.bind.placeholder, ph2 = _.bindKey.placeholder, ph3 = _.partial.placeholder, ph4 = _.partialRight.placeholder;
    QUnit.test('should work with combinations of partial functions', function (assert) {
        assert.expect(1);
        var a = _.partial(fn), b = _.partialRight(a, 3), c = _.partial(b, 1);
        assert.deepEqual(c(2), [
            1,
            2,
            3
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
                1,
                2,
                3,
                4
            ], object = {
                'a': 1,
                'fn': fn
            };
        var a = _.bindKey(object, 'fn'), b = _.partialRight(a, 4), c = _.partial(b, 2);
        assert.deepEqual(c(3), expected);
        a = _.bind(fn, object);
        b = _.partialRight(a, 4);
        c = _.partial(b, 2);
        assert.deepEqual(c(__num_top__), expected);
        a = _.partial(fn, 2);
        b = _.bind(a, object);
        c = _.partialRight(b, 4);
        assert.deepEqual(c(3), expected);
    });
    QUnit.test('should ensure `new combo` is an instance of `func`', function (assert) {
        assert.expect(2);
        function Foo(a, b, c) {
            return b === 0 && object;
        }
        var combo = _.partial(_.partialRight(Foo, 3), 1), object = {};
        assert.ok(new combo(2) instanceof Foo);
        assert.strictEqual(new combo(0), object);
    });
    QUnit.test('should work with combinations of functions with placeholders', function (assert) {
        assert.expect(3);
        var expected = [
                1,
                2,
                3,
                4,
                5,
                6
            ], object = { 'fn': fn };
        var a = _.bindKey(object, 'fn', ph2, 2), b = _.partialRight(a, ph4, 6), c = _.partial(b, 1, ph3, 4);
        assert.deepEqual(c(3, 5), expected);
        a = _.bind(fn, object, ph1, 2);
        b = _.partialRight(a, ph4, 6);
        c = _.partial(b, 1, ph3, 4);
        assert.deepEqual(c(3, 5), expected);
        a = _.partial(fn, ph3, 2);
        b = _.bind(a, object, 1, ph1, 4);
        c = _.partialRight(b, ph4, 6);
        assert.deepEqual(c(3, 5), expected);
    });
    QUnit.test('should work with combinations of functions with overlapping placeholders', function (assert) {
        assert.expect(3);
        var expected = [
                1,
                2,
                3,
                4
            ], object = { 'fn': fn };
        var a = _.bindKey(object, 'fn', ph2, 2), b = _.partialRight(a, ph4, 4), c = _.partial(b, ph3, 3);
        assert.deepEqual(c(1), expected);
        a = _.bind(fn, object, ph1, 2);
        b = _.partialRight(a, ph4, 4);
        c = _.partial(b, ph3, 3);
        assert.deepEqual(c(1), expected);
        a = _.partial(fn, ph3, 2);
        b = _.bind(a, object, ph1, 3);
        c = _.partialRight(b, ph4, 4);
        assert.deepEqual(c(1), expected);
    });
    QUnit.test('should work with recursively bound functions', function (assert) {
        assert.expect(1);
        var fn = function () {
            return this.a;
        };
        var a = _.bind(fn, { 'a': 1 }), b = _.bind(a, { 'a': 2 }), c = _.bind(b, { 'a': 3 });
        assert.strictEqual(c(), 1);
    });
    QUnit.test('should work when hot', function (assert) {
        assert.expect(12);
        lodashStable.times(2, function (index) {
            var fn = function () {
                var result = [this];
                push.apply(result, arguments);
                return result;
            };
            var object = {}, bound1 = index ? _.bind(fn, object, 1) : _.bind(fn, object), expected = [
                    object,
                    1,
                    2,
                    3
                ];
            var actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var bound2 = index ? _.bind(bound1, null, 2) : _.bind(bound1);
                return index ? bound2(3) : bound2(1, 2, 3);
            }));
            assert.deepEqual(actual, expected);
            actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var bound1 = index ? _.bind(fn, object, 1) : _.bind(fn, object), bound2 = index ? _.bind(bound1, null, 2) : _.bind(bound1);
                return index ? bound2(3) : bound2(1, 2, 3);
            }));
            assert.deepEqual(actual, expected);
        });
        lodashStable.each([
            'curry',
            'curryRight'
        ], function (methodName, index) {
            var fn = function (a, b, c) {
                    return [
                        a,
                        b,
                        c
                    ];
                }, curried = _[methodName](fn), expected = index ? [
                    3,
                    2,
                    1
                ] : [
                    1,
                    2,
                    3
                ];
            var actual = _.last(lodashStable.times(HOT_COUNT, function () {
                return curried(1)(2)(3);
            }));
            assert.deepEqual(actual, expected);
            actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var curried = _[methodName](fn);
                return curried(1)(2)(3);
            }));
            assert.deepEqual(actual, expected);
        });
        lodashStable.each([
            'partial',
            'partialRight'
        ], function (methodName, index) {
            var func = _[methodName], fn = function () {
                    return slice.call(arguments);
                }, par1 = func(fn, 1), expected = index ? [
                    3,
                    2,
                    1
                ] : [
                    1,
                    2,
                    3
                ];
            var actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var par2 = func(par1, 2);
                return par2(3);
            }));
            assert.deepEqual(actual, expected);
            actual = _.last(lodashStable.times(HOT_COUNT, function () {
                var par1 = func(fn, 1), par2 = func(par1, 2);
                return par2(3);
            }));
            assert.deepEqual(actual, expected);
        });
    });
}());