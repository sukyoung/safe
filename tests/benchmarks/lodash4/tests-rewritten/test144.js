QUnit.module('lodash.map');
(function () {
    var array = [
        __num_top__,
        __num_top__
    ];
    QUnit.test('should map values in `collection` to a new array', function (assert) {
        assert.expect(2);
        var object = {
                'a': __num_top__,
                'b': __num_top__
            }, expected = [
                __str_top__,
                __str_top__
            ];
        assert.deepEqual(_.map(array, String), expected);
        assert.deepEqual(_.map(object, String), expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            { 'a': __str_top__ },
            { 'a': __str_top__ }
        ];
        assert.deepEqual(_.map(objects, __str_top__), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should iterate over own string keyed properties of objects', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var actual = _.map(new Foo(), identity);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(2);
        var object = {
                'a': __num_top__,
                'b': __num_top__
            }, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                __num_top__,
                __num_top__
            ]));
        lodashStable.each([
            array,
            object
        ], function (collection) {
            var actual = lodashStable.map(values, function (value, index) {
                return index ? _.map(collection, value) : _.map(collection);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should accept a falsey `collection`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubArray);
        var actual = lodashStable.map(falsey, function (collection, index) {
            try {
                return index ? _.map(collection) : _.map();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.map(__num_top__), []);
    });
    QUnit.test('should treat a nodelist as an array-like object', function (assert) {
        assert.expect(1);
        if (document) {
            var actual = _.map(document.getElementsByTagName(__str_top__), function (element) {
                return element.nodeName.toLowerCase();
            });
            assert.deepEqual(actual, [__str_top__]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work with objects with non-number length properties', function (assert) {
        assert.expect(1);
        var value = { 'value': __str_top__ }, object = { 'length': { 'value': __str_top__ } };
        assert.deepEqual(_.map(object, identity), [value]);
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(array).map(noop) instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should provide correct `predicate` arguments in a lazy sequence', function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var args, array = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__), expected = [
                    __num_top__,
                    __num_top__,
                    _.map(array.slice(__num_top__), square)
                ];
            _(array).slice(__num_top__).map(function (value, index, array) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, [
                __num_top__,
                __num_top__,
                array.slice(__num_top__)
            ]);
            args = undefined;
            _(array).slice(__num_top__).map(square).map(function (value, index, array) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
            args = undefined;
            _(array).slice(__num_top__).map(square).map(function (value, index) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
            args = undefined;
            _(array).slice(__num_top__).map(square).map(function (value) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, [__num_top__]);
            args = undefined;
            _(array).slice(__num_top__).map(square).map(function () {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
        } else {
            skipAssert(assert, 5);
        }
    });
}());