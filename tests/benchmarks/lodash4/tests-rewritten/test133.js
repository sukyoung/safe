QUnit.module('custom `_.iteratee` methods');
(function () {
    var array = [
            __str_top__,
            __str_top__,
            __str_top__
        ], getPropA = _.partial(_.property, __str_top__), getPropB = _.partial(_.property, __str_top__), getLength = _.partial(_.property, __str_top__), iteratee = _.iteratee;
    var getSum = function () {
        return function (result, object) {
            return result + object.a;
        };
    };
    var objects = [
        {
            'a': __num_top__,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': __num_top__
        }
    ];
    QUnit.test('`_.countBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getLength;
            assert.deepEqual(_.countBy(array), {
                '3': __num_top__,
                '5': __num_top__
            });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.differenceBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.deepEqual(_.differenceBy(objects, [objects[__num_top__]]), [objects[__num_top__]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.dropRightWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.dropRightWhile(objects), objects.slice(__num_top__, __num_top__));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.dropWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.dropWhile(objects.reverse()).reverse(), objects.reverse().slice(__num_top__, __num_top__));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.every` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.every(objects.slice(__num_top__)), __bool_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.filter` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ];
            _.iteratee = getPropA;
            assert.deepEqual(_.filter(objects), [objects[__num_top__]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.find` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.find(objects), objects[__num_top__]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findIndex` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.findIndex(objects), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findLast` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.findLast(objects), objects[__num_top__]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findLastIndex` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.findLastIndex(objects), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findKey` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.findKey(objects), __str_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findLastKey` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.findLastKey(objects), __str_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.groupBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getLength;
            assert.deepEqual(_.groupBy(array), {
                '3': [
                    __str_top__,
                    __str_top__
                ],
                '5': [__str_top__]
            });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.intersectionBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.deepEqual(_.intersectionBy(objects, [objects[__num_top__]]), [objects[__num_top__]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.keyBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getLength;
            assert.deepEqual(_.keyBy(array), {
                '3': __str_top__,
                '5': __str_top__
            });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.map` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.deepEqual(_.map(objects), [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.mapKeys` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.mapKeys({ 'a': { 'b': __num_top__ } }), { '2': { 'b': __num_top__ } });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.mapValues` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.mapValues({ 'a': { 'b': __num_top__ } }), { 'a': __num_top__ });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.maxBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.maxBy(objects), objects[__num_top__]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.meanBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.meanBy(objects), __num_top__ / __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.minBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.minBy(objects), objects[__num_top__]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.partition` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ },
                { 'b': __num_top__ }
            ];
            _.iteratee = getPropA;
            assert.deepEqual(_.partition(objects), [
                objects.slice(__num_top__, __num_top__),
                objects.slice(__num_top__)
            ]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.pullAllBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.deepEqual(_.pullAllBy(objects.slice(), [{
                    'a': __num_top__,
                    'b': __num_top__
                }]), [objects[__num_top__]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.reduce` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getSum;
            assert.strictEqual(_.reduce(objects, undefined, __num_top__), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.reduceRight` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getSum;
            assert.strictEqual(_.reduceRight(objects, undefined, __num_top__), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.reject` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ];
            _.iteratee = getPropA;
            assert.deepEqual(_.reject(objects), [objects[__num_top__]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.remove` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ];
            _.iteratee = getPropA;
            _.remove(objects);
            assert.deepEqual(objects, [{ 'a': __num_top__ }]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.some` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.some(objects), __bool_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.sortBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.deepEqual(_.sortBy(objects.slice().reverse()), [
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__]
            ]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.sortedIndexBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ];
            _.iteratee = getPropA;
            assert.strictEqual(_.sortedIndexBy(objects, { 'a': __num_top__ }), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.sortedLastIndexBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ];
            _.iteratee = getPropA;
            assert.strictEqual(_.sortedLastIndexBy(objects, { 'a': __num_top__ }), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.sumBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.sumBy(objects), __num_top__);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.takeRightWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.takeRightWhile(objects), objects.slice(__num_top__));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.takeWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.takeWhile(objects.reverse()), objects.reverse().slice(__num_top__));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.transform` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = function () {
                return function (result, object) {
                    result.sum += object.a;
                };
            };
            assert.deepEqual(_.transform(objects, undefined, { 'sum': __num_top__ }), { 'sum': __num_top__ });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.uniqBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.uniqBy(objects), [
                objects[__num_top__],
                objects[__num_top__]
            ]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.unionBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.unionBy(objects.slice(__num_top__, __num_top__), [objects[__num_top__]]), [
                objects[__num_top__],
                objects[__num_top__]
            ]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.xorBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.deepEqual(_.xorBy(objects, objects.slice(__num_top__)), [objects[__num_top__]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
}());