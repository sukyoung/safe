QUnit.module('lodash.throttle');
(function () {
    QUnit.test('should throttle a function', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__, throttled = _.throttle(function () {
                callCount++;
            }, __num_top__);
        throttled();
        throttled();
        throttled();
        var lastCount = callCount;
        assert.ok(callCount);
        setTimeout(function () {
            assert.ok(callCount > lastCount);
            done();
        }, __num_top__);
    });
    QUnit.test('subsequent calls should return the result of the first call', function (assert) {
        assert.expect(5);
        var done = assert.async();
        var throttled = _.throttle(identity, __num_top__), results = [
                throttled(__str_top__),
                throttled(__str_top__)
            ];
        assert.deepEqual(results, [
            __str_top__,
            __str_top__
        ]);
        setTimeout(function () {
            var results = [
                throttled(__str_top__),
                throttled(__str_top__)
            ];
            assert.notEqual(results[__num_top__], __str_top__);
            assert.notStrictEqual(results[__num_top__], undefined);
            assert.notEqual(results[__num_top__], __str_top__);
            assert.notStrictEqual(results[__num_top__], undefined);
            done();
        }, __num_top__);
    });
    QUnit.test('should clear timeout when `func` is called', function (assert) {
        assert.expect(1);
        var done = assert.async();
        if (!isModularize) {
            var callCount = __num_top__, dateCount = __num_top__;
            var lodash = _.runInContext({
                'Date': {
                    'now': function () {
                        return ++dateCount == __num_top__ ? Infinity : +new Date();
                    }
                }
            });
            var throttled = lodash.throttle(function () {
                callCount++;
            }, __num_top__);
            throttled();
            throttled();
            setTimeout(function () {
                assert.strictEqual(callCount, __num_top__);
                done();
            }, __num_top__);
        } else {
            skipAssert(assert);
            done();
        }
    });
    QUnit.test('should not trigger a trailing call when invoked once', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__, throttled = _.throttle(function () {
                callCount++;
            }, __num_top__);
        throttled();
        assert.strictEqual(callCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    lodashStable.times(__num_top__, function (index) {
        QUnit.test(__str_top__ + (index ? __str_top__ : __str_top__), function (assert) {
            assert.expect(1);
            var done = assert.async();
            var callCount = __num_top__, limit = argv || isPhantom ? __num_top__ : __num_top__, options = index ? { 'leading': __bool_top__ } : {}, throttled = _.throttle(function () {
                    callCount++;
                }, __num_top__, options);
            var start = +new Date();
            while (new Date() - start < limit) {
                throttled();
            }
            var actual = callCount > __num_top__;
            setTimeout(function () {
                assert.ok(actual);
                done();
            }, __num_top__);
        });
    });
    QUnit.test('should trigger a second throttled call as soon as possible', function (assert) {
        assert.expect(3);
        var done = assert.async();
        var callCount = __num_top__;
        var throttled = _.throttle(function () {
            callCount++;
        }, __num_top__, { 'leading': __bool_top__ });
        throttled();
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            throttled();
        }, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
        }, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should apply default options', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__, throttled = _.throttle(function () {
                callCount++;
            }, __num_top__, {});
        throttled();
        throttled();
        assert.strictEqual(callCount, __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should support a `leading` option', function (assert) {
        assert.expect(2);
        var withLeading = _.throttle(identity, __num_top__, { 'leading': __bool_top__ });
        assert.strictEqual(withLeading(__str_top__), __str_top__);
        var withoutLeading = _.throttle(identity, __num_top__, { 'leading': __bool_top__ });
        assert.strictEqual(withoutLeading(__str_top__), undefined);
    });
    QUnit.test('should support a `trailing` option', function (assert) {
        assert.expect(6);
        var done = assert.async();
        var withCount = __num_top__, withoutCount = __num_top__;
        var withTrailing = _.throttle(function (value) {
            withCount++;
            return value;
        }, __num_top__, { 'trailing': __bool_top__ });
        var withoutTrailing = _.throttle(function (value) {
            withoutCount++;
            return value;
        }, __num_top__, { 'trailing': __bool_top__ });
        assert.strictEqual(withTrailing(__str_top__), __str_top__);
        assert.strictEqual(withTrailing(__str_top__), __str_top__);
        assert.strictEqual(withoutTrailing(__str_top__), __str_top__);
        assert.strictEqual(withoutTrailing(__str_top__), __str_top__);
        setTimeout(function () {
            assert.strictEqual(withCount, __num_top__);
            assert.strictEqual(withoutCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should not update `lastCalled`, at the end of the timeout, when `trailing` is `false`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var callCount = __num_top__;
        var throttled = _.throttle(function () {
            callCount++;
        }, __num_top__, { 'trailing': __bool_top__ });
        throttled();
        throttled();
        setTimeout(function () {
            throttled();
            throttled();
        }, __num_top__);
        setTimeout(function () {
            assert.ok(callCount > __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test('should work with a system time of `0`', function (assert) {
        assert.expect(3);
        var done = assert.async();
        if (!isModularize) {
            var callCount = __num_top__, dateCount = __num_top__;
            var lodash = _.runInContext({
                'Date': {
                    'now': function () {
                        return ++dateCount < __num_top__ ? __num_top__ : +new Date();
                    }
                }
            });
            var throttled = lodash.throttle(function (value) {
                callCount++;
                return value;
            }, __num_top__);
            var results = [
                throttled(__str_top__),
                throttled(__str_top__),
                throttled(__str_top__)
            ];
            assert.deepEqual(results, [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
            assert.strictEqual(callCount, __num_top__);
            setTimeout(function () {
                assert.strictEqual(callCount, __num_top__);
                done();
            }, __num_top__);
        } else {
            skipAssert(assert, 3);
            done();
        }
    });
}());