QUnit.module('lodash.map');
(function () {
    var array = [
        1,
        2
    ];
    QUnit.test('should map values in `collection` to a new array', function (assert) {
        assert.expect(2);
        var object = {
                'a': 1,
                'b': 2
            }, expected = [
                '1',
                '2'
            ];
        assert.deepEqual(_.map(array, String), expected);
        assert.deepEqual(_.map(object, String), expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            { 'a': 'x' },
            { 'a': 'y' }
        ];
        assert.deepEqual(_.map(objects, 'a'), [
            'x',
            'y'
        ]);
    });
    QUnit.test('should iterate over own string keyed properties of objects', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var actual = _.map(new Foo(), identity);
        assert.deepEqual(actual, [1]);
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(2);
        var object = {
                'a': 1,
                'b': 2
            }, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                1,
                2
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
        assert.deepEqual(_.map(1), []);
    });
    QUnit.test('should treat a nodelist as an array-like object', function (assert) {
        assert.expect(1);
        if (document) {
            var actual = _.map(document.getElementsByTagName('body'), function (element) {
                return element.nodeName.toLowerCase();
            });
            assert.deepEqual(actual, ['body']);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work with objects with non-number length properties', function (assert) {
        assert.expect(1);
        var value = { 'value': 'x' }, object = { 'length': { 'value': 'x' } };
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
            var args, array = lodashStable.range(LARGE_ARRAY_SIZE + 1), expected = [
                    1,
                    0,
                    _.map(array.slice(1), square)
                ];
            _(array).slice(1).map(function (value, index, array) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, [
                1,
                0,
                array.slice(1)
            ]);
            args = undefined;
            _(array).slice(1).map(square).map(function (value, index, array) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
            args = undefined;
            _(array).slice(1).map(square).map(function (value, index) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
            args = undefined;
            _(array).slice(__num_top__).map(square).map(function (value) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, [1]);
            args = undefined;
            _(array).slice(1).map(square).map(function () {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
        } else {
            skipAssert(assert, 5);
        }
    });
}());