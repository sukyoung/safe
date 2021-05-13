QUnit.module('lodash.debounce and lodash.throttle');
lodashStable.each([
    'debounce',
    'throttle'
], function (methodName) {
    var func = _[methodName], isDebounce = methodName == 'debounce';
    QUnit.test('`_.' + methodName + '` should not error for non-object `options` values', function (assert) {
        assert.expect(1);
        func(noop, 32, 1);
        assert.ok(true);
    });
    QUnit.test('`_.' + methodName + '` should use a default `wait` of `0`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var callCount = 0, funced = func(function () {
                callCount++;
            });
        funced();
        setTimeout(function () {
            funced();
            assert.strictEqual(callCount, isDebounce ? 1 : 2);
            done();
        }, 32);
    });
    QUnit.test('`_.' + methodName + '` should invoke `func` with the correct `this` binding', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var actual = [], object = {
                'funced': func(function () {
                    actual.push(this);
                }, 32)
            }, expected = lodashStable.times(isDebounce ? 1 : 2, lodashStable.constant(object));
        object.funced();
        if (!isDebounce) {
            object.funced();
        }
        setTimeout(function () {
            assert.deepEqual(actual, expected);
            done();
        }, 64);
    });
    QUnit.test('`_.' + methodName + '` supports recursive calls', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var actual = [], args = lodashStable.map([
                'a',
                'b',
                'c'
            ], function (chr) {
                return [
                    {},
                    chr
                ];
            }), expected = args.slice(), queue = args.slice();
        var funced = func(function () {
            var current = [this];
            push.apply(current, arguments);
            actual.push(current);
            var next = queue.shift();
            if (next) {
                funced.call(next[0], next[1]);
            }
        }, 32);
        var next = queue.shift();
        funced.call(next[0], next[1]);
        assert.deepEqual(actual, expected.slice(0, isDebounce ? 0 : 1));
        setTimeout(function () {
            assert.deepEqual(actual, expected.slice(0, actual.length));
            done();
        }, 256);
    });
    QUnit.test('`_.' + methodName + '` should work if the system time is set backwards', function (assert) {
        assert.expect(1);
        var done = assert.async();
        if (!isModularize) {
            var callCount = 0, dateCount = 0;
            var lodash = _.runInContext({
                'Date': {
                    'now': function () {
                        return ++dateCount == 4 ? +new Date(__num_top__, 3, 23, 23, 27, 18) : +new Date();
                    }
                }
            });
            var funced = lodash[methodName](function () {
                callCount++;
            }, 32);
            funced();
            setTimeout(function () {
                funced();
                assert.strictEqual(callCount, isDebounce ? 1 : 2);
                done();
            }, 64);
        } else {
            skipAssert(assert);
            done();
        }
    });
    QUnit.test('`_.' + methodName + '` should support cancelling delayed calls', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var callCount = __num_top__;
        var funced = func(function () {
            callCount++;
        }, 32, { 'leading': false });
        funced();
        funced.cancel();
        setTimeout(function () {
            assert.strictEqual(callCount, 0);
            done();
        }, 64);
    });
    QUnit.test('`_.' + methodName + '` should reset `lastCalled` after cancelling', function (assert) {
        assert.expect(3);
        var done = assert.async();
        var callCount = 0;
        var funced = func(function () {
            return ++callCount;
        }, 32, { 'leading': true });
        assert.strictEqual(funced(), 1);
        funced.cancel();
        assert.strictEqual(funced(), 2);
        funced();
        setTimeout(function () {
            assert.strictEqual(callCount, 3);
            done();
        }, __num_top__);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = 0;
        var funced = func(function () {
            return ++callCount;
        }, __num_top__, { 'leading': false });
        funced();
        assert.strictEqual(funced.flush(), 1);
        setTimeout(function () {
            assert.strictEqual(callCount, 1);
            done();
        }, 64);
    });
    QUnit.test('`_.' + methodName + '` should noop `cancel` and `flush` when nothing is queued', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = 0, funced = func(function () {
                callCount++;
            }, 32);
        funced.cancel();
        assert.strictEqual(funced.flush(), undefined);
        setTimeout(function () {
            assert.strictEqual(callCount, 0);
            done();
        }, 64);
    });
});