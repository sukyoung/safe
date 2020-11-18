QUnit.module('custom `_.iteratee` methods');
(function () {
    var array = [
            'one',
            'two',
            'three'
        ], getPropA = _.partial(_.property, 'a'), getPropB = _.partial(_.property, 'b'), getLength = _.partial(_.property, 'length'), iteratee = _.iteratee;
    var getSum = function () {
        return function (result, object) {
            return result + object.a;
        };
    };
    var objects = [
        {
            'a': 0,
            'b': 0
        },
        {
            'a': 1,
            'b': 0
        },
        {
            'a': 1,
            'b': 1
        }
    ];
    QUnit.test('`_.countBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getLength;
            assert.deepEqual(_.countBy(array), {
                '3': 2,
                '5': 1
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
            assert.deepEqual(_.differenceBy(objects, [objects[1]]), [objects[0]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.dropRightWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.dropRightWhile(objects), objects.slice(0, 2));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.dropWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.dropWhile(objects.reverse()).reverse(), objects.reverse().slice(0, 2));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.every` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.every(objects.slice(1)), true);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.filter` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': 0 },
                { 'a': 1 }
            ];
            _.iteratee = getPropA;
            assert.deepEqual(_.filter(objects), [objects[1]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.find` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.find(objects), objects[1]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findIndex` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.findIndex(objects), 1);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findLast` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.findLast(objects), objects[2]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findLastIndex` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.findLastIndex(objects), 2);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findKey` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.findKey(objects), '2');
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.findLastKey` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.findLastKey(objects), '2');
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
                    'one',
                    'two'
                ],
                '5': ['three']
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
            assert.deepEqual(_.intersectionBy(objects, [objects[2]]), [objects[1]]);
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
                '3': 'two',
                '5': 'three'
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
                0,
                1,
                1
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
            assert.deepEqual(_.mapKeys({ 'a': { 'b': 2 } }), { '2': { 'b': 2 } });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.mapValues` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.mapValues({ 'a': { 'b': 2 } }), { 'a': 2 });
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.maxBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.maxBy(objects), objects[2]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.meanBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropA;
            assert.strictEqual(_.meanBy(objects), 2 / 3);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.minBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.minBy(objects), objects[0]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.partition` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': 1 },
                { 'a': 1 },
                { 'b': 2 }
            ];
            _.iteratee = getPropA;
            assert.deepEqual(_.partition(objects), [
                objects.slice(0, 2),
                objects.slice(2)
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
                    'a': 1,
                    'b': 0
                }]), [objects[0]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.reduce` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getSum;
            assert.strictEqual(_.reduce(objects, undefined, 0), 2);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.reduceRight` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getSum;
            assert.strictEqual(_.reduceRight(objects, undefined, 0), 2);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.reject` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': 0 },
                { 'a': 1 }
            ];
            _.iteratee = getPropA;
            assert.deepEqual(_.reject(objects), [objects[0]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.remove` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': 0 },
                { 'a': 1 }
            ];
            _.iteratee = getPropA;
            _.remove(objects);
            assert.deepEqual(objects, [{ 'a': 0 }]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.some` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.some(objects), true);
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
                objects[0],
                objects[2],
                objects[1]
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
                { 'a': 30 },
                { 'a': 50 }
            ];
            _.iteratee = getPropA;
            assert.strictEqual(_.sortedIndexBy(objects, { 'a': 40 }), 1);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.sortedLastIndexBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var objects = [
                { 'a': 30 },
                { 'a': 50 }
            ];
            _.iteratee = getPropA;
            assert.strictEqual(_.sortedLastIndexBy(objects, { 'a': 40 }), 1);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.sumBy` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.strictEqual(_.sumBy(objects), 1);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.takeRightWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.takeRightWhile(objects), objects.slice(2));
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.takeWhile` should use `_.iteratee` internally', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            _.iteratee = getPropB;
            assert.deepEqual(_.takeWhile(objects.reverse()), objects.reverse().slice(2));
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
            assert.deepEqual(_.transform(objects, undefined, { 'sum': 0 }), { 'sum': 2 });
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
                objects[0],
                objects[2]
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
            assert.deepEqual(_.unionBy(objects.slice(0, 1), [objects[__num_top__]]), [
                objects[0],
                objects[2]
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
            assert.deepEqual(_.xorBy(objects, objects.slice(1)), [objects[0]]);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
}());