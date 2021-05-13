QUnit.module('lodash methods');
(function () {
    var allMethods = lodashStable.reject(_.functions(_).sort(), function (methodName) {
        return lodashStable.startsWith(methodName, __str_top__);
    });
    var checkFuncs = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var noBinding = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var rejectFalsey = [
        __str_top__,
        __str_top__
    ].concat(checkFuncs);
    var returnArrays = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var acceptFalsey = lodashStable.difference(allMethods, rejectFalsey);
    QUnit.test('should accept falsey arguments', function (assert) {
        assert.expect(316);
        var arrays = lodashStable.map(falsey, stubArray);
        lodashStable.each(acceptFalsey, function (methodName) {
            var expected = arrays, func = _[methodName];
            var actual = lodashStable.map(falsey, function (value, index) {
                return index ? func(value) : func();
            });
            if (methodName == __str_top__) {
                root._ = oldDash;
            } else if (methodName == __str_top__ || methodName == __str_top__) {
                expected = falsey;
            }
            if (lodashStable.includes(returnArrays, methodName) && methodName != __str_top__) {
                assert.deepEqual(actual, expected, __str_top__ + methodName + __str_top__);
            }
            assert.ok(__bool_top__, __str_top__ + methodName + __str_top__);
        });
        lodashStable.each([
            __str_top__,
            __str_top__,
            __str_top__
        ], function (methodName) {
            if (!_[methodName]) {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should return an array', function (assert) {
        assert.expect(70);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        lodashStable.each(returnArrays, function (methodName) {
            var actual, func = _[methodName];
            switch (methodName) {
            case __str_top__:
                actual = func(array, __str_top__);
                break;
            case __str_top__:
                actual = func(array, __num_top__);
                break;
            default:
                actual = func(array);
            }
            assert.ok(lodashStable.isArray(actual), __str_top__ + methodName + __str_top__);
            var isPull = methodName == __str_top__ || methodName == __str_top__;
            assert.strictEqual(actual === array, isPull, __str_top__ + methodName + __str_top__ + (isPull ? __str_top__ : __str_top__) + __str_top__);
        });
    });
    QUnit.test('should throw an error for falsey arguments', function (assert) {
        assert.expect(24);
        lodashStable.each(rejectFalsey, function (methodName) {
            var expected = lodashStable.map(falsey, stubTrue), func = _[methodName];
            var actual = lodashStable.map(falsey, function (value, index) {
                var pass = !index && /^(?:backflow|compose|cond|flow(Right)?|over(?:Every|Some)?)$/.test(methodName);
                try {
                    index ? func(value) : func();
                } catch (e) {
                    pass = !pass && e instanceof TypeError && (!lodashStable.includes(checkFuncs, methodName) || e.message == FUNC_ERROR_TEXT);
                }
                return pass;
            });
            assert.deepEqual(actual, expected, __str_top__ + methodName + __str_top__);
        });
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(30);
        lodashStable.each(noBinding, function (methodName) {
            var fn = function () {
                    return this.a;
                }, func = _[methodName], isNegate = methodName == __str_top__, object = { 'a': __num_top__ }, expected = isNegate ? __bool_top__ : __num_top__;
            var wrapper = func(_.bind(fn, object));
            assert.strictEqual(wrapper(), expected, __str_top__ + methodName + __str_top__);
            wrapper = _.bind(func(fn), object);
            assert.strictEqual(wrapper(), expected, __str_top__ + methodName + __str_top__);
            object.wrapper = func(fn);
            assert.strictEqual(object.wrapper(), expected, __str_top__ + methodName + __str_top__);
        });
    });
    QUnit.test('should not contain minified method names (test production builds)', function (assert) {
        assert.expect(1);
        var shortNames = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.ok(lodashStable.every(_.functions(_), function (methodName) {
            return methodName.length > __num_top__ || lodashStable.includes(shortNames, methodName);
        }));
    });
}());