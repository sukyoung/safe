QUnit.module('lodash.at');
(function () {
    var array = [
            'a',
            'b',
            'c'
        ], object = {
            'a': [
                { 'b': { 'c': 3 } },
                4
            ]
        };
    QUnit.test('should return the elements corresponding to the specified keys', function (assert) {
        assert.expect(1);
        var actual = _.at(array, [
            0,
            2
        ]);
        assert.deepEqual(actual, [
            'a',
            'c'
        ]);
    });
    QUnit.test('should return `undefined` for nonexistent keys', function (assert) {
        assert.expect(1);
        var actual = _.at(array, [
            2,
            4,
            0
        ]);
        assert.deepEqual(actual, [
            'c',
            undefined,
            'a'
        ]);
    });
    QUnit.test('should work with non-index keys on array values', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(empties, function (value) {
            return value === 0 || lodashStable.isArray(value);
        }).concat(-1, 1.1);
        var array = lodashStable.transform(values, function (result, value) {
            result[value] = 1;
        }, []);
        var expected = lodashStable.map(values, stubOne), actual = _.at(array, values);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an empty array when no keys are given', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.at(array), []);
        assert.deepEqual(_.at(array, [], []), []);
    });
    QUnit.test('should accept multiple key arguments', function (assert) {
        assert.expect(1);
        var actual = _.at([
            'a',
            'b',
            'c',
            'd'
        ], 3, 0, 2);
        assert.deepEqual(actual, [
            'd',
            'a',
            'c'
        ]);
    });
    QUnit.test('should work with a falsey `object` when keys are given', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant(Array(4)));
        var actual = lodashStable.map(falsey, function (object) {
            try {
                return _.at(object, 0, 1, 'pop', 'push');
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with an `arguments` object for `object`', function (assert) {
        assert.expect(1);
        var actual = _.at(args, [
            2,
            0
        ]);
        assert.deepEqual(actual, [
            3,
            1
        ]);
    });
    QUnit.test('should work with `arguments` object as secondary arguments', function (assert) {
        assert.expect(1);
        var actual = _.at([
            1,
            2,
            3,
            4,
            5
        ], args);
        assert.deepEqual(actual, [
            2,
            3,
            4
        ]);
    });
    QUnit.test('should work with an object for `object`', function (assert) {
        assert.expect(1);
        var actual = _.at(object, [
            'a[0].b.c',
            'a[1]'
        ]);
        assert.deepEqual(actual, [
            3,
            4
        ]);
    });
    QUnit.test('should pluck inherited property values', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var actual = _.at(new Foo(), 'b');
        assert.deepEqual(actual, [2]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var largeArray = lodashStable.range(LARGE_ARRAY_SIZE), smallArray = array;
            lodashStable.each([
                [2],
                ['2'],
                [
                    2,
                    1
                ]
            ], function (paths) {
                lodashStable.times(2, function (index) {
                    var array = index ? largeArray : smallArray, wrapped = _(array).map(identity).at(paths);
                    assert.deepEqual(wrapped.value(), _.at(_.map(array, identity), paths));
                });
            });
        } else {
            skipAssert(assert, 6);
        }
    });
    QUnit.test('should support shortcut fusion', function (assert) {
        assert.expect(8);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), count = 0, iteratee = function (value) {
                    count++;
                    return square(value);
                }, lastIndex = LARGE_ARRAY_SIZE - 1;
            lodashStable.each([
                lastIndex,
                lastIndex + '',
                LARGE_ARRAY_SIZE,
                []
            ], function (n, index) {
                count = 0;
                var actual = _(array).map(iteratee).at(n).value(), expected = index < 2 ? 1 : 0;
                assert.strictEqual(count, expected);
                expected = index == 3 ? [] : [index == 2 ? undefined : square(lastIndex)];
                assert.deepEqual(actual, expected);
            });
        } else {
            skipAssert(assert, 8);
        }
    });
    QUnit.test('work with an object for `object` when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var paths = [
                    'a[0].b.c',
                    __str_top__
                ], actual = _(object).map(identity).at(paths).value();
            assert.deepEqual(actual, _.at(_.map(object, identity), paths));
            var indexObject = { '0': 1 };
            actual = _(indexObject).at(0).value();
            assert.deepEqual(actual, _.at(indexObject, 0));
        } else {
            skipAssert(assert, 2);
        }
    });
}());