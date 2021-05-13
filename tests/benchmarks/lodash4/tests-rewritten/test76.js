QUnit.module('object assignments');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isAssign = methodName == __str_top__, isDefaults = /^defaults/.test(methodName);
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(primitives, function (value) {
            var object = Object(value);
            object.a = __num_top__;
            return object;
        });
        var actual = lodashStable.map(primitives, function (value) {
            return func(value, { 'a': __num_top__ });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isAssign ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var expected = isAssign ? { 'a': __num_top__ } : {
            'a': __num_top__,
            'b': __num_top__
        };
        assert.deepEqual(func({}, new Foo()), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function fn() {
        }
        fn.b = __num_top__;
        assert.deepEqual(func({}, { 'a': __num_top__ }, fn), {
            'a': __num_top__,
            'b': __num_top__
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        try {
            assert.deepEqual(func({ 'a': __num_top__ }, undefined, { 'b': __num_top__ }, null), {
                'a': __num_top__,
                'b': __num_top__
            });
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
                { 'a': __num_top__ },
                { 'b': __num_top__ },
                { 'c': __num_top__ }
            ], expected = {
                'a': isDefaults ? __num_top__ : __num_top__,
                'b': __num_top__,
                'c': __num_top__
            };
        function fn() {
        }
        ;
        fn.a = array[__num_top__];
        fn.b = array[__num_top__];
        fn.c = array[__num_top__];
        assert.deepEqual(lodashStable.reduce(array, func, { 'a': __num_top__ }), expected);
        assert.deepEqual(lodashStable.reduce(fn, func, { 'a': __num_top__ }), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _({ 'a': __num_top__ }), actual = wrapped[methodName]({ 'b': __num_top__ });
            assert.notStrictEqual(actual, wrapped);
        } else {
            skipAssert(assert);
        }
    });
});
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.a = __num_top__;
        var actual = func(new Foo(), { 'b': __num_top__ });
        assert.notOk(_.has(actual, __str_top__));
    });
});
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__],
            { 'a': __num_top__ },
            NaN
        ], function (value) {
            var object = {}, pass = __bool_top__;
            defineProperty(object, __str_top__, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'get': lodashStable.constant(value),
                'set': function () {
                    pass = __bool_top__;
                }
            });
            func(object, { 'a': value });
            assert.ok(pass);
        });
    });
});
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isMergeWith = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var args, object = { 'a': __num_top__ }, source = { 'a': __num_top__ }, expected = lodashStable.map([
                __num_top__,
                __num_top__,
                __str_top__,
                object,
                source
            ], lodashStable.cloneDeep);
        func(object, source, function () {
            args || (args = lodashStable.map(slice.call(arguments, __num_top__, __num_top__), lodashStable.cloneDeep));
        });
        assert.deepEqual(args, expected, __str_top__);
        var argsList = [], objectValue = [
                __num_top__,
                __num_top__
            ], sourceValue = { 'b': __num_top__ };
        object = { 'a': objectValue };
        source = { 'a': sourceValue };
        expected = [lodashStable.map([
                objectValue,
                sourceValue,
                __str_top__,
                object,
                source
            ], lodashStable.cloneDeep)];
        if (isMergeWith) {
            expected.push(lodashStable.map([
                undefined,
                __num_top__,
                __str_top__,
                objectValue,
                sourceValue
            ], lodashStable.cloneDeep));
        }
        func(object, source, function () {
            argsList.push(lodashStable.map(slice.call(arguments, __num_top__, __num_top__), lodashStable.cloneDeep));
        });
        assert.deepEqual(argsList, expected, __str_top__);
        args = undefined;
        object = { 'a': __num_top__ };
        source = { 'b': __num_top__ };
        expected = lodashStable.map([
            undefined,
            __num_top__,
            __str_top__,
            object,
            source
        ], lodashStable.cloneDeep);
        func(object, source, function () {
            args || (args = lodashStable.map(slice.call(arguments, __num_top__, __num_top__), lodashStable.cloneDeep));
        });
        assert.deepEqual(args, expected, __str_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        function callback() {
        }
        callback.b = __num_top__;
        var actual = func({ 'a': __num_top__ }, callback);
        assert.deepEqual(actual, {
            'a': __num_top__,
            'b': __num_top__
        });
        actual = func({ 'a': __num_top__ }, callback, { 'c': __num_top__ });
        assert.deepEqual(actual, {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        });
    });
});