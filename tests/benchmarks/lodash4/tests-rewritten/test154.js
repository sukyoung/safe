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
                __str_top__,
                key
            ]);
            return entry && entry.value;
        },
        'has': function (key) {
            return lodashStable.some(this.__data__, [
                __str_top__,
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
        assert.strictEqual(memoized(__num_top__, __num_top__, __num_top__), __num_top__);
        assert.strictEqual(memoized(__num_top__, __num_top__, __num_top__), __num_top__);
    });
    QUnit.test('should support a `resolver`', function (assert) {
        assert.expect(2);
        var fn = function (a, b, c) {
                return a + b + c;
            }, memoized = _.memoize(fn, fn);
        assert.strictEqual(memoized(__num_top__, __num_top__, __num_top__), __num_top__);
        assert.strictEqual(memoized(__num_top__, __num_top__, __num_top__), __num_top__);
    });
    QUnit.test('should use `this` binding of function for `resolver`', function (assert) {
        assert.expect(2);
        var fn = function (a, b, c) {
                return a + this.b + this.c;
            }, memoized = _.memoize(fn, fn);
        var object = {
            'memoized': memoized,
            'b': __num_top__,
            'c': __num_top__
        };
        assert.strictEqual(object.memoized(__num_top__), __num_top__);
        object.b = __num_top__;
        object.c = __num_top__;
        assert.strictEqual(object.memoized(__num_top__), __num_top__);
    });
    QUnit.test('should throw a TypeError if `resolve` is truthy and not a function', function (assert) {
        assert.expect(1);
        assert.raises(function () {
            _.memoize(noop, __bool_top__);
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
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
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
        var array = [], key = __str_top__;
        lodashStable.times(__num_top__, function (index) {
            var count = __num_top__, resolver = index ? identity : undefined;
            var memoized = _.memoize(function () {
                count++;
                return array;
            }, resolver);
            var cache = memoized.cache;
            memoized(key);
            memoized(key);
            assert.strictEqual(count, __num_top__);
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
        var cache = memoized.cache, key1 = { 'id': __str_top__ }, key2 = { 'id': __str_top__ };
        assert.strictEqual(memoized(key1), __str_top__);
        assert.strictEqual(cache.has(key1), __bool_top__);
        assert.strictEqual(memoized(key2), __str_top__);
        assert.strictEqual(cache.has(key2), __bool_top__);
        _.memoize.Cache = oldCache;
    });
    QUnit.test('should works with an immutable `_.memoize.Cache` ', function (assert) {
        assert.expect(2);
        var oldCache = _.memoize.Cache;
        _.memoize.Cache = ImmutableCache;
        var memoized = _.memoize(function (object) {
            return object.id;
        });
        var key1 = { 'id': __str_top__ }, key2 = { 'id': __str_top__ };
        memoized(key1);
        memoized(key2);
        var cache = memoized.cache;
        assert.strictEqual(cache.has(key1), __bool_top__);
        assert.strictEqual(cache.has(key2), __bool_top__);
        _.memoize.Cache = oldCache;
    });
}());