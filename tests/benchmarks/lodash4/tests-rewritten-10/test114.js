QUnit.module('lodash.isMatchWith');
(function () {
    QUnit.test('should provide correct `customizer` arguments', function (assert) {
        assert.expect(1);
        var argsList = [], object1 = {
                'a': [
                    __num_top__,
                    2
                ],
                'b': null
            }, object2 = {
                'a': [
                    1,
                    2
                ],
                'b': null
            };
        object1.b = object2;
        object2.b = object1;
        var expected = [
            [
                object1.a,
                object2.a,
                'a',
                object1,
                object2
            ],
            [
                object1.a[__num_top__],
                object2.a[0],
                0,
                object1.a,
                object2.a
            ],
            [
                object1.a[__num_top__],
                object2.a[1],
                1,
                object1.a,
                object2.a
            ],
            [
                object1.b,
                object2.b,
                'b',
                object1,
                object2
            ],
            [
                object1.b.a,
                object2.b.a,
                'a',
                object1.b,
                object2.b
            ],
            [
                object1.b.a[0],
                object2.b.a[0],
                0,
                object1.b.a,
                object2.b.a
            ],
            [
                object1.b.a[1],
                object2.b.a[1],
                1,
                object1.b.a,
                object2.b.a
            ],
            [
                object1.b.b,
                object2.b.b,
                'b',
                object1.b,
                object2.b
            ]
        ];
        _.isMatchWith(object1, object2, function (assert) {
            argsList.push(slice.call(arguments, 0, -1));
        });
        assert.deepEqual(argsList, expected);
    });
    QUnit.test('should handle comparisons when `customizer` returns `undefined`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isMatchWith({ 'a': 1 }, { 'a': 1 }, noop), true);
    });
    QUnit.test('should not handle comparisons when `customizer` returns `true`', function (assert) {
        assert.expect(2);
        var customizer = function (value) {
            return _.isString(value) || undefined;
        };
        assert.strictEqual(_.isMatchWith(['a'], ['b'], customizer), true);
        assert.strictEqual(_.isMatchWith({ '0': 'a' }, { '0': 'b' }, customizer), true);
    });
    QUnit.test('should not handle comparisons when `customizer` returns `false`', function (assert) {
        assert.expect(2);
        var customizer = function (value) {
            return _.isString(value) ? false : undefined;
        };
        assert.strictEqual(_.isMatchWith(['a'], ['a'], customizer), false);
        assert.strictEqual(_.isMatchWith({ '0': __str_top__ }, { '0': 'a' }, customizer), false);
    });
    QUnit.test('should return a boolean value even when `customizer` does not', function (assert) {
        assert.expect(2);
        var object = { 'a': 1 }, actual = _.isMatchWith(object, { 'a': 1 }, stubA);
        assert.strictEqual(actual, true);
        var expected = lodashStable.map(falsey, stubFalse);
        actual = [];
        lodashStable.each(falsey, function (value) {
            actual.push(_.isMatchWith(object, { 'a': 2 }, lodashStable.constant(value)));
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should provide `stack` to `customizer`', function (assert) {
        assert.expect(1);
        var actual;
        _.isMatchWith({ 'a': 1 }, { 'a': 1 }, function () {
            actual = _.last(arguments);
        });
        assert.ok(isNpm ? actual.constructor.name == __str_top__ : actual instanceof mapCaches.Stack);
    });
    QUnit.test('should ensure `customizer` is a function', function (assert) {
        assert.expect(1);
        var object = { 'a': 1 }, matches = _.partial(_.isMatchWith, object), actual = lodashStable.map([
                object,
                { 'a': 2 }
            ], matches);
        assert.deepEqual(actual, [
            true,
            false
        ]);
    });
    QUnit.test('should call `customizer` for values maps and sets', function (assert) {
        assert.expect(2);
        var value = { 'a': { 'b': __num_top__ } };
        if (Map) {
            var map1 = new Map();
            map1.set('a', value);
            var map2 = new Map();
            map2.set('a', value);
        }
        if (Set) {
            var set1 = new Set();
            set1.add(value);
            var set2 = new Set();
            set2.add(value);
        }
        lodashStable.each([
            [
                map1,
                map2
            ],
            [
                set1,
                set2
            ]
        ], function (pair, index) {
            if (pair[0]) {
                var argsList = [], array = lodashStable.toArray(pair[0]), object1 = { 'a': pair[__num_top__] }, object2 = { 'a': pair[1] };
                var expected = [
                    [
                        pair[__num_top__],
                        pair[1],
                        'a',
                        object1,
                        object2
                    ],
                    [
                        array[0],
                        array[0],
                        0,
                        array,
                        array
                    ],
                    [
                        array[0][0],
                        array[0][0],
                        0,
                        array[0],
                        array[0]
                    ],
                    [
                        array[0][1],
                        array[0][__num_top__],
                        1,
                        array[0],
                        array[0]
                    ]
                ];
                if (index) {
                    expected.length = 2;
                }
                _.isMatchWith({ 'a': pair[0] }, { 'a': pair[1] }, function () {
                    argsList.push(slice.call(arguments, 0, -1));
                });
                assert.deepEqual(argsList, expected, index ? __str_top__ : 'Map');
            } else {
                skipAssert(assert);
            }
        });
    });
}());