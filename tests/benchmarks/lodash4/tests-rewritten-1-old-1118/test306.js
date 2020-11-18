QUnit.module('lodash methods');
(function () {
    var allMethods = lodashStable.reject(_.functions(_).sort(), function (methodName) {
        return lodashStable.startsWith(methodName, '_');
    });
    var checkFuncs = [
        'after',
        'ary',
        'before',
        'bind',
        'curry',
        'curryRight',
        'debounce',
        'defer',
        'delay',
        'flip',
        'flow',
        'flowRight',
        'memoize',
        'negate',
        'once',
        'partial',
        'partialRight',
        'rearg',
        'rest',
        'spread',
        'throttle',
        'unary'
    ];
    var noBinding = [
        'flip',
        'memoize',
        'negate',
        'once',
        'overArgs',
        'partial',
        __str_top__,
        'rearg',
        'rest',
        'spread'
    ];
    var rejectFalsey = [
        'tap',
        'thru'
    ].concat(checkFuncs);
    var returnArrays = [
        'at',
        'chunk',
        'compact',
        'difference',
        'drop',
        'filter',
        'flatten',
        'functions',
        'initial',
        'intersection',
        'invokeMap',
        'keys',
        'map',
        'orderBy',
        'pull',
        'pullAll',
        'pullAt',
        'range',
        'rangeRight',
        'reject',
        'remove',
        'shuffle',
        'sortBy',
        'tail',
        'take',
        'times',
        'toArray',
        'toPairs',
        'toPairsIn',
        'union',
        'uniq',
        'values',
        'without',
        'xor',
        'zip'
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
            if (methodName == 'noConflict') {
                root._ = oldDash;
            } else if (methodName == 'pull' || methodName == 'pullAll') {
                expected = falsey;
            }
            if (lodashStable.includes(returnArrays, methodName) && methodName != 'sample') {
                assert.deepEqual(actual, expected, '_.' + methodName + ' returns an array');
            }
            assert.ok(true, '`_.' + methodName + '` accepts falsey arguments');
        });
        lodashStable.each([
            'chain',
            'noConflict',
            'runInContext'
        ], function (methodName) {
            if (!_[methodName]) {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should return an array', function (assert) {
        assert.expect(70);
        var array = [
            1,
            2,
            3
        ];
        lodashStable.each(returnArrays, function (methodName) {
            var actual, func = _[methodName];
            switch (methodName) {
            case 'invokeMap':
                actual = func(array, 'toFixed');
                break;
            case 'sample':
                actual = func(array, 1);
                break;
            default:
                actual = func(array);
            }
            assert.ok(lodashStable.isArray(actual), '_.' + methodName + ' returns an array');
            var isPull = methodName == 'pull' || methodName == 'pullAll';
            assert.strictEqual(actual === array, isPull, '_.' + methodName + ' should ' + (isPull ? '' : 'not ') + 'return the given array');
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
            assert.deepEqual(actual, expected, '`_.' + methodName + '` rejects falsey arguments');
        });
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(30);
        lodashStable.each(noBinding, function (methodName) {
            var fn = function () {
                    return this.a;
                }, func = _[methodName], isNegate = methodName == 'negate', object = { 'a': 1 }, expected = isNegate ? false : 1;
            var wrapper = func(_.bind(fn, object));
            assert.strictEqual(wrapper(), expected, '`_.' + methodName + '` can consume a bound function');
            wrapper = _.bind(func(fn), object);
            assert.strictEqual(wrapper(), expected, '`_.' + methodName + '` can be bound');
            object.wrapper = func(fn);
            assert.strictEqual(object.wrapper(), expected, '`_.' + methodName + '` uses the `this` of its parent object');
        });
    });
    QUnit.test('should not contain minified method names (test production builds)', function (assert) {
        assert.expect(1);
        var shortNames = [
            '_',
            'at',
            'eq',
            'gt',
            'lt'
        ];
        assert.ok(lodashStable.every(_.functions(_), function (methodName) {
            return methodName.length > 2 || lodashStable.includes(shortNames, methodName);
        }));
    });
}());