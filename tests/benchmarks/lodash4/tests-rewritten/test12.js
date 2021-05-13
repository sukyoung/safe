QUnit.module('lodash.at');
(function () {
    var array = [
            __str_top__,
            __str_top__,
            __str_top__
        ], object = {
            'a': [
                { 'b': { 'c': __num_top__ } },
                __num_top__
            ]
        };
    QUnit.test('should return the elements corresponding to the specified keys', function (assert) {
        assert.expect(1);
        var actual = _.at(array, [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should return `undefined` for nonexistent keys', function (assert) {
        assert.expect(1);
        var actual = _.at(array, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __str_top__,
            undefined,
            __str_top__
        ]);
    });
    QUnit.test('should work with non-index keys on array values', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(empties, function (value) {
            return value === __num_top__ || lodashStable.isArray(value);
        }).concat(-__num_top__, __num_top__);
        var array = lodashStable.transform(values, function (result, value) {
            result[value] = __num_top__;
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
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ], __num_top__, __num_top__, __num_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with a falsey `object` when keys are given', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant(Array(__num_top__)));
        var actual = lodashStable.map(falsey, function (object) {
            try {
                return _.at(object, __num_top__, __num_top__, __str_top__, __str_top__);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with an `arguments` object for `object`', function (assert) {
        assert.expect(1);
        var actual = _.at(args, [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with `arguments` object as secondary arguments', function (assert) {
        assert.expect(1);
        var actual = _.at([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], args);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with an object for `object`', function (assert) {
        assert.expect(1);
        var actual = _.at(object, [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should pluck inherited property values', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var actual = _.at(new Foo(), __str_top__);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var largeArray = lodashStable.range(LARGE_ARRAY_SIZE), smallArray = array;
            lodashStable.each([
                [__num_top__],
                [__str_top__],
                [
                    __num_top__,
                    __num_top__
                ]
            ], function (paths) {
                lodashStable.times(__num_top__, function (index) {
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
            var array = lodashStable.range(LARGE_ARRAY_SIZE), count = __num_top__, iteratee = function (value) {
                    count++;
                    return square(value);
                }, lastIndex = LARGE_ARRAY_SIZE - __num_top__;
            lodashStable.each([
                lastIndex,
                lastIndex + __str_top__,
                LARGE_ARRAY_SIZE,
                []
            ], function (n, index) {
                count = __num_top__;
                var actual = _(array).map(iteratee).at(n).value(), expected = index < __num_top__ ? __num_top__ : __num_top__;
                assert.strictEqual(count, expected);
                expected = index == __num_top__ ? [] : [index == __num_top__ ? undefined : square(lastIndex)];
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
                    __str_top__,
                    __str_top__
                ], actual = _(object).map(identity).at(paths).value();
            assert.deepEqual(actual, _.at(_.map(object, identity), paths));
            var indexObject = { '0': __num_top__ };
            actual = _(indexObject).at(__num_top__).value();
            assert.deepEqual(actual, _.at(indexObject, __num_top__));
        } else {
            skipAssert(assert, 2);
        }
    });
}());