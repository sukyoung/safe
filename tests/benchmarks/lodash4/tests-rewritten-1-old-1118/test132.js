QUnit.module('lodash.iteratee');
(function () {
    QUnit.test('should provide arguments to `func`', function (assert) {
        assert.expect(1);
        var fn = function () {
                return slice.call(arguments);
            }, iteratee = _.iteratee(fn), actual = iteratee('a', 'b', 'c', 'd', 'e', 'f');
        assert.deepEqual(actual, [
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
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
            'a': 1,
            'b': 2
        });
        assert.strictEqual(matches({
            'a': __num_top__,
            'b': 2,
            'c': 3
        }), true);
        assert.strictEqual(matches({ 'b': 2 }), false);
    });
    QUnit.test('should not change `_.matches` behavior if `source` is modified', function (assert) {
        assert.expect(9);
        var sources = [
            {
                'a': {
                    'b': 2,
                    'c': 3
                }
            },
            {
                'a': 1,
                'b': 2
            },
            { 'a': 1 }
        ];
        lodashStable.each(sources, function (source, index) {
            var object = lodashStable.cloneDeep(source), matches = _.iteratee(source);
            assert.strictEqual(matches(object), true);
            if (index) {
                source.a = 2;
                source.b = 1;
                source.c = 3;
            } else {
                source.a.b = 1;
                source.a.c = 2;
                source.a.d = 3;
            }
            assert.strictEqual(matches(object), true);
            assert.strictEqual(matches(source), false);
        });
    });
    QUnit.test('should return an iteratee created by `_.matchesProperty` when `func` is an array', function (assert) {
        assert.expect(3);
        var array = [
                'a',
                undefined
            ], matches = _.iteratee([
                0,
                'a'
            ]);
        assert.strictEqual(matches(array), true);
        matches = _.iteratee([
            '0',
            'a'
        ]);
        assert.strictEqual(matches(array), true);
        matches = _.iteratee([
            1,
            undefined
        ]);
        assert.strictEqual(matches(array), true);
    });
    QUnit.test('should support deep paths for `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        var object = {
                'a': {
                    'b': {
                        'c': 1,
                        'd': 2
                    }
                }
            }, matches = _.iteratee([
                'a.b',
                { 'c': 1 }
            ]);
        assert.strictEqual(matches(object), true);
    });
    QUnit.test('should not change `_.matchesProperty` behavior if `source` is modified', function (assert) {
        assert.expect(9);
        var sources = [
            {
                'a': {
                    'b': 2,
                    'c': 3
                }
            },
            {
                'a': 1,
                'b': 2
            },
            { 'a': 1 }
        ];
        lodashStable.each(sources, function (source, index) {
            var object = { 'a': lodashStable.cloneDeep(source) }, matches = _.iteratee([
                    'a',
                    source
                ]);
            assert.strictEqual(matches(object), true);
            if (index) {
                source.a = 2;
                source.b = 1;
                source.c = 3;
            } else {
                source.a.b = 1;
                source.a.c = 2;
                source.a.d = 3;
            }
            assert.strictEqual(matches(object), true);
            assert.strictEqual(matches({ 'a': source }), false);
        });
    });
    QUnit.test('should return an iteratee created by `_.property` when `func` is a number or string', function (assert) {
        assert.expect(2);
        var array = ['a'], prop = _.iteratee(0);
        assert.strictEqual(prop(array), 'a');
        prop = _.iteratee('0');
        assert.strictEqual(prop(array), 'a');
    });
    QUnit.test('should support deep paths for `_.property` shorthands', function (assert) {
        assert.expect(1);
        var object = { 'a': { 'b': 2 } }, prop = _.iteratee('a.b');
        assert.strictEqual(prop(object), 2);
    });
    QUnit.test('should work with functions created by `_.partial` and `_.partialRight`', function (assert) {
        assert.expect(2);
        var fn = function () {
            var result = [this.a];
            push.apply(result, arguments);
            return result;
        };
        var expected = [
                1,
                2,
                3
            ], object = {
                'a': 1,
                'iteratee': _.iteratee(_.partial(fn, 2))
            };
        assert.deepEqual(object.iteratee(3), expected);
        object.iteratee = _.iteratee(_.partialRight(fn, 3));
        assert.deepEqual(object.iteratee(2), expected);
    });
    QUnit.test('should use internal `iteratee` if external is unavailable', function (assert) {
        assert.expect(1);
        var iteratee = _.iteratee;
        delete _.iteratee;
        assert.deepEqual(_.map([{ 'a': 1 }], 'a'), [1]);
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