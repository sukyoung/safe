QUnit.module('lodash.iteratee');
(function () {
    QUnit.test('should provide arguments to `func`', function (assert) {
        assert.expect(1);
        var fn = function () {
                return slice.call(arguments);
            }, iteratee = _.iteratee(fn), actual = iteratee(__str_top__, __str_top__, __str_top__, __str_top__, __str_top__, __str_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should return `_.identity` when `func` is nullish', function (assert) {
        assert.expect(1);
        var object = {}, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                !isNpm && _.identity,
                object
            ]));
        var actual = lodashStable.map(values, function (value, index) {
            var identity = index ? _.iteratee(value) : _.iteratee();
            return [
                !isNpm && identity,
                identity(object)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an iteratee created by `_.matches` when `func` is an object', function (assert) {
        assert.expect(2);
        var matches = _.iteratee({
            'a': __num_top__,
            'b': __num_top__
        });
        assert.strictEqual(matches({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __bool_top__);
        assert.strictEqual(matches({ 'b': __num_top__ }), __bool_top__);
    });
    QUnit.test('should not change `_.matches` behavior if `source` is modified', function (assert) {
        assert.expect(9);
        var sources = [
            {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            },
            {
                'a': __num_top__,
                'b': __num_top__
            },
            { 'a': __num_top__ }
        ];
        lodashStable.each(sources, function (source, index) {
            var object = lodashStable.cloneDeep(source), matches = _.iteratee(source);
            assert.strictEqual(matches(object), __bool_top__);
            if (index) {
                source.a = __num_top__;
                source.b = __num_top__;
                source.c = __num_top__;
            } else {
                source.a.b = __num_top__;
                source.a.c = __num_top__;
                source.a.d = __num_top__;
            }
            assert.strictEqual(matches(object), __bool_top__);
            assert.strictEqual(matches(source), __bool_top__);
        });
    });
    QUnit.test('should return an iteratee created by `_.matchesProperty` when `func` is an array', function (assert) {
        assert.expect(3);
        var array = [
                __str_top__,
                undefined
            ], matches = _.iteratee([
                __num_top__,
                __str_top__
            ]);
        assert.strictEqual(matches(array), __bool_top__);
        matches = _.iteratee([
            __str_top__,
            __str_top__
        ]);
        assert.strictEqual(matches(array), __bool_top__);
        matches = _.iteratee([
            __num_top__,
            undefined
        ]);
        assert.strictEqual(matches(array), __bool_top__);
    });
    QUnit.test('should support deep paths for `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        var object = {
                'a': {
                    'b': {
                        'c': __num_top__,
                        'd': __num_top__
                    }
                }
            }, matches = _.iteratee([
                __str_top__,
                { 'c': __num_top__ }
            ]);
        assert.strictEqual(matches(object), __bool_top__);
    });
    QUnit.test('should not change `_.matchesProperty` behavior if `source` is modified', function (assert) {
        assert.expect(9);
        var sources = [
            {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            },
            {
                'a': __num_top__,
                'b': __num_top__
            },
            { 'a': __num_top__ }
        ];
        lodashStable.each(sources, function (source, index) {
            var object = { 'a': lodashStable.cloneDeep(source) }, matches = _.iteratee([
                    __str_top__,
                    source
                ]);
            assert.strictEqual(matches(object), __bool_top__);
            if (index) {
                source.a = __num_top__;
                source.b = __num_top__;
                source.c = __num_top__;
            } else {
                source.a.b = __num_top__;
                source.a.c = __num_top__;
                source.a.d = __num_top__;
            }
            assert.strictEqual(matches(object), __bool_top__);
            assert.strictEqual(matches({ 'a': source }), __bool_top__);
        });
    });
    QUnit.test('should return an iteratee created by `_.property` when `func` is a number or string', function (assert) {
        assert.expect(2);
        var array = [__str_top__], prop = _.iteratee(__num_top__);
        assert.strictEqual(prop(array), __str_top__);
        prop = _.iteratee(__str_top__);
        assert.strictEqual(prop(array), __str_top__);
    });
    QUnit.test('should support deep paths for `_.property` shorthands', function (assert) {
        assert.expect(1);
        var object = { 'a': { 'b': __num_top__ } }, prop = _.iteratee(__str_top__);
        assert.strictEqual(prop(object), __num_top__);
    });
    QUnit.test('should work with functions created by `_.partial` and `_.partialRight`', function (assert) {
        assert.expect(2);
        var fn = function () {
            var result = [this.a];
            push.apply(result, arguments);
            return result;
        };
        var expected = [
                __num_top__,
                __num_top__,
                __num_top__
            ], object = {
                'a': __num_top__,
                'iteratee': _.iteratee(_.partial(fn, __num_top__))
            };
        assert.deepEqual(object.iteratee(__num_top__), expected);
        object.iteratee = _.iteratee(_.partialRight(fn, __num_top__));
        assert.deepEqual(object.iteratee(__num_top__), expected);
    });
    QUnit.test('should use internal `iteratee` if external is unavailable', function (assert) {
        assert.expect(1);
        var iteratee = _.iteratee;
        delete _.iteratee;
        assert.deepEqual(_.map([{ 'a': __num_top__ }], __str_top__), [__num_top__]);
        _.iteratee = iteratee;
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var fn = function () {
                return this instanceof Number;
            }, array = [
                fn,
                fn,
                fn
            ], iteratees = lodashStable.map(array, _.iteratee), expected = lodashStable.map(array, stubFalse);
        var actual = lodashStable.map(iteratees, function (iteratee) {
            return iteratee();
        });
        assert.deepEqual(actual, expected);
    });
}());