QUnit.module('lodash.debounce');
(function () {
    QUnit.test('should debounce a function', function (assert) {
        assert.expect(6);
        var done = assert.async();
        var callCount = __num_top__;
        var debounced = _.debounce(function (value) {
            ++callCount;
            return value;
        }, __num_top__);
        var results = [
            debounced(__str_top__),
            debounced(__str_top__),
            debounced(__str_top__)
        ];
        assert.deepEqual(results, [
            undefined,
            undefined,
            undefined
        ]);
        assert.strictEqual(callCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            var results = [
                debounced(__str_top__),
                debounced(__str_top__),
                debounced(__str_top__)
            ];
            assert.deepEqual(results, [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
            assert.strictEqual(callCount, __num_top__);
        }, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('subsequent debounced calls return the last `func` result', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var debounced = _.debounce(identity, __num_top__);
        debounced(__str_top__);
        setTimeout(function () {
            assert.notEqual(debounced(__str_top__), __str_top__);
        }, __num_top__);
        setTimeout(function () {
            assert.notEqual(debounced(__str_top__), __str_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should not immediately call `func` when `wait` is `0`', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__, debounced = _.debounce(function () {
                ++callCount;
            }, __num_top__);
        debounced();
        debounced();
        assert.strictEqual(callCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should apply default options', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__, debounced = _.debounce(function () {
                callCount++;
            }, __num_top__, {});
        debounced();
        assert.strictEqual(callCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should support a `leading` option', function (assert) {
        assert.expect(4);
        var done = assert.async();
        var callCounts = [
            __num_top__,
            __num_top__
        ];
        var withLeading = _.debounce(function () {
            callCounts[__num_top__]++;
        }, __num_top__, { 'leading': __bool_top__ });
        var withLeadingAndTrailing = _.debounce(function () {
            callCounts[__num_top__]++;
        }, __num_top__, { 'leading': __bool_top__ });
        withLeading();
        assert.strictEqual(callCounts[__num_top__], __num_top__);
        withLeadingAndTrailing();
        withLeadingAndTrailing();
        assert.strictEqual(callCounts[__num_top__], __num_top__);
        setTimeout(function () {
            assert.deepEqual(callCounts, [
                __num_top__,
                __num_top__
            ]);
            withLeading();
            assert.strictEqual(callCounts[__num_top__], __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('subsequent leading debounced calls return the last `func` result', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var debounced = _.debounce(identity, __num_top__, {
                'leading': __bool_top__,
                'trailing': __bool_top__
            }), results = [
                debounced(__str_top__),
                debounced(__str_top__)
            ];
        assert.deepEqual(results, [
            __str_top__,
            __str_top__
        ]);
        setTimeout(function () {
            var results = [
                debounced(__str_top__),
                debounced(__str_top__)
            ];
            assert.deepEqual(results, [
                __str_top__,
                __str_top__
            ]);
            done();
        }, __num_top__);
    });
    QUnit.test('should support a `trailing` option', function (assert) {
        assert.expect(4);
        var done = assert.async();
        var withCount = __num_top__, withoutCount = __num_top__;
        var withTrailing = _.debounce(function () {
            withCount++;
        }, __num_top__, { 'trailing': __bool_top__ });
        var withoutTrailing = _.debounce(function () {
            withoutCount++;
        }, __num_top__, { 'trailing': __bool_top__ });
        withTrailing();
        assert.strictEqual(withCount, __num_top__);
        withoutTrailing();
        assert.strictEqual(withoutCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(withCount, __num_top__);
            assert.strictEqual(withoutCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should support a `maxWait` option', function (assert) {
        assert.expect(4);
        var done = assert.async();
        var callCount = __num_top__;
        var debounced = _.debounce(function (value) {
            ++callCount;
            return value;
        }, __num_top__, { 'maxWait': __num_top__ });
        debounced();
        debounced();
        assert.strictEqual(callCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            debounced();
            debounced();
            assert.strictEqual(callCount, __num_top__);
        }, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should support `maxWait` in a tight loop', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var limit = argv || isPhantom ? __num_top__ : __num_top__, withCount = __num_top__, withoutCount = __num_top__;
        var withMaxWait = _.debounce(function () {
            withCount++;
        }, __num_top__, { 'maxWait': __num_top__ });
        var withoutMaxWait = _.debounce(function () {
            withoutCount++;
        }, __num_top__);
        var start = +new Date();
        while (new Date() - start < limit) {
            withMaxWait();
            withoutMaxWait();
        }
        var actual = [
            Boolean(withoutCount),
            Boolean(withCount)
        ];
        setTimeout(function () {
            assert.deepEqual(actual, [
                __bool_top__,
                __bool_top__
            ]);
            done();
        }, __num_top__);
    });
    QUnit.test('should queue a trailing call for subsequent debounced calls after `maxWait`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var callCount = __num_top__;
        var debounced = _.debounce(function () {
            ++callCount;
        }, __num_top__, { 'maxWait': __num_top__ });
        debounced();
        setTimeout(debounced, __num_top__);
        setTimeout(debounced, __num_top__);
        setTimeout(debounced, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should cancel `maxDelayed` when `delayed` is invoked', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__;
        var debounced = _.debounce(function () {
            callCount++;
        }, __num_top__, { 'maxWait': __num_top__ });
        debounced();
        setTimeout(function () {
            debounced();
            assert.strictEqual(callCount, __num_top__);
        }, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should invoke the trailing call with the correct arguments and `this` binding', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var actual, callCount = __num_top__, object = {};
        var debounced = _.debounce(function (value) {
            actual = [this];
            push.apply(actual, arguments);
            return ++callCount != __num_top__;
        }, __num_top__, {
            'leading': __bool_top__,
            'maxWait': __num_top__
        });
        while (__bool_top__) {
            if (!debounced.call(object, __str_top__)) {
                break;
            }
        }
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            assert.deepEqual(actual, [
                object,
                __str_top__
            ]);
            done();
        }, __num_top__);
    });
}());