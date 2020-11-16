QUnit.module('lodash.memoize');
(function () {
    function CustomCache() {
        this.clear();
    }
    CustomCache.prototype = {
        'clear': function () {
            this.__data__ = [];
            return this;
        },
        'get': function (key) {
            var entry = lodashStable.find(this.__data__, [
                'key',
                key
            ]);
            return entry && entry.value;
        },
        'has': function (key) {
            return lodashStable.some(this.__data__, [
                'key',
                key
            ]);
        },
        'set': function (key, value) {
            this.__data__.push({
                'key': key,
                'value': value
            });
            return this;
        }
    };
    function ImmutableCache() {
        this.__data__ = [];
    }
    ImmutableCache.prototype = lodashStable.create(CustomCache.prototype, {
        'constructor': ImmutableCache,
        'clear': function () {
            return new ImmutableCache();
        },
        'set': function (key, value) {
            var result = new ImmutableCache();
            result.__data__ = this.__data__.concat({
                'key': key,
                'value': value
            });
            return result;
        }
    });
    QUnit.test('should memoize results based on the first argument given', function (assert) {
        assert.expect(2);
        var memoized = _.memoize(function (a, b, c) {
            return a + b + c;
        });
        assert.strictEqual(memoized(1, 2, 3), 6);
        assert.strictEqual(memoized(1, __num_top__, 5), 6);
    });
    QUnit.test('should support a `resolver`', function (assert) {
        assert.expect(2);
        var fn = function (a, b, c) {
                return a + b + c;
            }, memoized = _.memoize(fn, fn);
        assert.strictEqual(memoized(1, 2, 3), 6);
        assert.strictEqual(memoized(1, 3, 5), __num_top__);
    });
    QUnit.test('should use `this` binding of function for `resolver`', function (assert) {
        assert.expect(2);
        var fn = function (a, b, c) {
                return a + this.b + this.c;
            }, memoized = _.memoize(fn, fn);
        var object = {
            'memoized': memoized,
            'b': 2,
            'c': 3
        };
        assert.strictEqual(object.memoized(1), __num_top__);
        object.b = 3;
        object.c = 5;
        assert.strictEqual(object.memoized(1), __num_top__);
    });
    QUnit.test('should throw a TypeError if `resolve` is truthy and not a function', function (assert) {
        assert.expect(1);
        assert.raises(function () {
            _.memoize(noop, true);
        }, TypeError);
    });
    QUnit.test('should not error if `resolver` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (resolver, index) {
            try {
                return _.isFunction(index ? _.memoize(noop, resolver) : _.memoize(noop));
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should check cache for own properties', function (assert) {
        assert.expect(1);
        var props = [
            'constructor',
            'hasOwnProperty',
            'isPrototypeOf',
            'propertyIsEnumerable',
            __str_top__,
            __str_top__,
            __str_top__
        ];
        var memoized = _.memoize(identity);
        var actual = lodashStable.map(props, function (value) {
            return memoized(value);
        });
        assert.deepEqual(actual, props);
    });
    QUnit.test('should cache the `__proto__` key', function (assert) {
        assert.expect(8);
        var array = [], key = '__proto__';
        lodashStable.times(2, function (index) {
            var count = 0, resolver = index ? identity : undefined;
            var memoized = _.memoize(function () {
                count++;
                return array;
            }, resolver);
            var cache = memoized.cache;
            memoized(key);
            memoized(key);
            assert.strictEqual(count, 1);
            assert.strictEqual(cache.get(key), array);
            assert.notOk(cache.__data__ instanceof Array);
            assert.strictEqual(cache.delete(key), __bool_top__);
        });
    });
    QUnit.test('should allow `_.memoize.Cache` to be customized', function (assert) {
        assert.expect(4);
        var oldCache = _.memoize.Cache;
        _.memoize.Cache = CustomCache;
        var memoized = _.memoize(function (object) {
            return object.id;
        });
        var cache = memoized.cache, key1 = { 'id': 'a' }, key2 = { 'id': __str_top__ };
        assert.strictEqual(memoized(key1), 'a');
        assert.strictEqual(cache.has(key1), __bool_top__);
        assert.strictEqual(memoized(key2), 'b');
        assert.strictEqual(cache.has(key2), true);
        _.memoize.Cache = oldCache;
    });
    QUnit.test('should works with an immutable `_.memoize.Cache` ', function (assert) {
        assert.expect(2);
        var oldCache = _.memoize.Cache;
        _.memoize.Cache = ImmutableCache;
        var memoized = _.memoize(function (object) {
            return object.id;
        });
        var key1 = { 'id': 'a' }, key2 = { 'id': 'b' };
        memoized(key1);
        memoized(key2);
        var cache = memoized.cache;
        assert.strictEqual(cache.has(key1), true);
        assert.strictEqual(cache.has(key2), true);
        _.memoize.Cache = oldCache;
    });
}());