QUnit.module('lodash.debounce and lodash.throttle');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isDebounce = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        func(noop, __num_top__, __num_top__);
        assert.ok(__bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var done = assert.async();
        var callCount = __num_top__, funced = func(function () {
                callCount++;
            });
        funced();
        setTimeout(function () {
            funced();
            assert.strictEqual(callCount, isDebounce ? __num_top__ : __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var done = assert.async();
        var actual = [], object = {
                'funced': func(function () {
                    actual.push(this);
                }, __num_top__)
            }, expected = lodashStable.times(isDebounce ? __num_top__ : __num_top__, lodashStable.constant(object));
        object.funced();
        if (!isDebounce) {
            object.funced();
        }
        setTimeout(function () {
            assert.deepEqual(actual, expected);
            done();
        }, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var done = assert.async();
        var actual = [], args = lodashStable.map([
                __str_top__,
                __str_top__,
                __str_top__
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
                funced.call(next[__num_top__], next[__num_top__]);
            }
        }, __num_top__);
        var next = queue.shift();
        funced.call(next[__num_top__], next[__num_top__]);
        assert.deepEqual(actual, expected.slice(__num_top__, isDebounce ? __num_top__ : __num_top__));
        setTimeout(function () {
            assert.deepEqual(actual, expected.slice(__num_top__, actual.length));
            done();
        }, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var done = assert.async();
        if (!isModularize) {
            var callCount = __num_top__, dateCount = __num_top__;
            var lodash = _.runInContext({
                'Date': {
                    'now': function () {
                        return ++dateCount == __num_top__ ? +new Date(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__) : +new Date();
                    }
                }
            });
            var funced = lodash[methodName](function () {
                callCount++;
            }, __num_top__);
            funced();
            setTimeout(function () {
                funced();
                assert.strictEqual(callCount, isDebounce ? __num_top__ : __num_top__);
                done();
            }, __num_top__);
        } else {
            skipAssert(assert);
            done();
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var done = assert.async();
        var callCount = __num_top__;
        var funced = func(function () {
            callCount++;
        }, __num_top__, { 'leading': __bool_top__ });
        funced();
        funced.cancel();
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var done = assert.async();
        var callCount = __num_top__;
        var funced = func(function () {
            return ++callCount;
        }, __num_top__, { 'leading': __bool_top__ });
        assert.strictEqual(funced(), __num_top__);
        funced.cancel();
        assert.strictEqual(funced(), __num_top__);
        funced();
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__;
        var funced = func(function () {
            return ++callCount;
        }, __num_top__, { 'leading': __bool_top__ });
        funced();
        assert.strictEqual(funced.flush(), __num_top__);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var done = assert.async();
        var callCount = __num_top__, funced = func(function () {
                callCount++;
            }, __num_top__);
        funced.cancel();
        assert.strictEqual(funced.flush(), undefined);
        setTimeout(function () {
            assert.strictEqual(callCount, __num_top__);
            done();
        }, __num_top__);
    });
});