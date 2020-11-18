QUnit.module('object assignments');
lodashStable.each([
    'assign',
    'assignIn',
    'defaults',
    'defaultsDeep',
    'merge'
], function (methodName) {
    var func = _[methodName], isAssign = methodName == 'assign', isDefaults = /^defaults/.test(methodName);
    QUnit.test('`_.' + methodName + '` should coerce primitives to objects', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(primitives, function (value) {
            var object = Object(value);
            object.a = 1;
            return object;
        });
        var actual = lodashStable.map(primitives, function (value) {
            return func(value, { 'a': 1 });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should assign own ' + (isAssign ? '' : 'and inherited ') + 'string keyed source properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var expected = isAssign ? { 'a': 1 } : {
            'a': 1,
            'b': 2
        };
        assert.deepEqual(func({}, new Foo()), expected);
    });
    QUnit.test('`_.' + methodName + '` should not skip a trailing function source', function (assert) {
        assert.expect(1);
        function fn() {
        }
        fn.b = 2;
        assert.deepEqual(func({}, { 'a': 1 }, fn), {
            'a': 1,
            'b': 2
        });
    });
    QUnit.test('`_.' + methodName + '` should not error on nullish sources', function (assert) {
        assert.expect(1);
        try {
            assert.deepEqual(func({ 'a': 1 }, undefined, { 'b': 2 }, null), {
                'a': 1,
                'b': 2
            });
        } catch (e) {
            assert.ok(false, e.message);
        }
    });
    QUnit.test('`_.' + methodName + '` should create an object when `object` is nullish', function (assert) {
        assert.expect(2);
        var source = { 'a': __num_top__ }, values = [
                null,
                undefined
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            var object = func(value, source);
            return object !== source && lodashStable.isEqual(object, source);
        });
        assert.deepEqual(actual, expected);
        actual = lodashStable.map(values, function (value) {
            return lodashStable.isEqual(func(value), {});
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work as an iteratee for methods like `_.reduce`', function (assert) {
        assert.expect(2);
        var array = [
                { 'a': 1 },
                { 'b': 2 },
                { 'c': 3 }
            ], expected = {
                'a': isDefaults ? 0 : 1,
                'b': 2,
                'c': 3
            };
        function fn() {
        }
        ;
        fn.a = array[0];
        fn.b = array[1];
        fn.c = array[2];
        assert.deepEqual(lodashStable.reduce(array, func, { 'a': 0 }), expected);
        assert.deepEqual(lodashStable.reduce(fn, func, { 'a': 0 }), expected);
    });
    QUnit.test('`_.' + methodName + '` should not return the existing wrapped value when chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _({ 'a': 1 }), actual = wrapped[methodName]({ 'b': 2 });
            assert.notStrictEqual(actual, wrapped);
        } else {
            skipAssert(assert);
        }
    });
});
lodashStable.each([
    'assign',
    'assignIn',
    'merge'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should not treat `object` as `source`', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.a = 1;
        var actual = func(new Foo(), { 'b': 2 });
        assert.notOk(_.has(actual, 'a'));
    });
});
lodashStable.each([
    'assign',
    'assignIn',
    'assignInWith',
    'assignWith',
    'defaults',
    'defaultsDeep',
    'merge',
    'mergeWith'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should not assign values that are the same as their destinations', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a',
            ['a'],
            { 'a': 1 },
            NaN
        ], function (value) {
            var object = {}, pass = true;
            defineProperty(object, 'a', {
                'configurable': true,
                'enumerable': true,
                'get': lodashStable.constant(value),
                'set': function () {
                    pass = false;
                }
            });
            func(object, { 'a': value });
            assert.ok(pass);
        });
    });
});
lodashStable.each([
    'assignWith',
    'assignInWith',
    'mergeWith'
], function (methodName) {
    var func = _[methodName], isMergeWith = methodName == 'mergeWith';
    QUnit.test('`_.' + methodName + '` should provide correct `customizer` arguments', function (assert) {
        assert.expect(3);
        var args, object = { 'a': 1 }, source = { 'a': 2 }, expected = lodashStable.map([
                1,
                2,
                'a',
                object,
                source
            ], lodashStable.cloneDeep);
        func(object, source, function () {
            args || (args = lodashStable.map(slice.call(arguments, 0, 5), lodashStable.cloneDeep));
        });
        assert.deepEqual(args, expected, 'primitive values');
        var argsList = [], objectValue = [
                1,
                2
            ], sourceValue = { 'b': 2 };
        object = { 'a': objectValue };
        source = { 'a': sourceValue };
        expected = [lodashStable.map([
                objectValue,
                sourceValue,
                'a',
                object,
                source
            ], lodashStable.cloneDeep)];
        if (isMergeWith) {
            expected.push(lodashStable.map([
                undefined,
                2,
                'b',
                objectValue,
                sourceValue
            ], lodashStable.cloneDeep));
        }
        func(object, source, function () {
            argsList.push(lodashStable.map(slice.call(arguments, 0, 5), lodashStable.cloneDeep));
        });
        assert.deepEqual(argsList, expected, 'object values');
        args = undefined;
        object = { 'a': 1 };
        source = { 'b': 2 };
        expected = lodashStable.map([
            undefined,
            2,
            'b',
            object,
            source
        ], lodashStable.cloneDeep);
        func(object, source, function () {
            args || (args = lodashStable.map(slice.call(arguments, 0, 5), lodashStable.cloneDeep));
        });
        assert.deepEqual(args, expected, 'undefined properties');
    });
    QUnit.test('`_.' + methodName + '` should not treat the second argument as a `customizer` callback', function (assert) {
        assert.expect(2);
        function callback() {
        }
        callback.b = 2;
        var actual = func({ 'a': 1 }, callback);
        assert.deepEqual(actual, {
            'a': 1,
            'b': 2
        });
        actual = func({ 'a': 1 }, callback, { 'c': 3 });
        assert.deepEqual(actual, {
            'a': 1,
            'b': 2,
            'c': 3
        });
    });
});