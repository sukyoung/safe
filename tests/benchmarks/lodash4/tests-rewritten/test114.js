QUnit.module('lodash.isMatchWith');
(function () {
    QUnit.test('should provide correct `customizer` arguments', function (assert) {
        assert.expect(1);
        var argsList = [], object1 = {
                'a': [
                    __num_top__,
                    __num_top__
                ],
                'b': null
            }, object2 = {
                'a': [
                    __num_top__,
                    __num_top__
                ],
                'b': null
            };
        object1.b = object2;
        object2.b = object1;
        var expected = [
            [
                object1.a,
                object2.a,
                __str_top__,
                object1,
                object2
            ],
            [
                object1.a[__num_top__],
                object2.a[__num_top__],
                __num_top__,
                object1.a,
                object2.a
            ],
            [
                object1.a[__num_top__],
                object2.a[__num_top__],
                __num_top__,
                object1.a,
                object2.a
            ],
            [
                object1.b,
                object2.b,
                __str_top__,
                object1,
                object2
            ],
            [
                object1.b.a,
                object2.b.a,
                __str_top__,
                object1.b,
                object2.b
            ],
            [
                object1.b.a[__num_top__],
                object2.b.a[__num_top__],
                __num_top__,
                object1.b.a,
                object2.b.a
            ],
            [
                object1.b.a[__num_top__],
                object2.b.a[__num_top__],
                __num_top__,
                object1.b.a,
                object2.b.a
            ],
            [
                object1.b.b,
                object2.b.b,
                __str_top__,
                object1.b,
                object2.b
            ]
        ];
        _.isMatchWith(object1, object2, function (assert) {
            argsList.push(slice.call(arguments, __num_top__, -__num_top__));
        });
        assert.deepEqual(argsList, expected);
    });
    QUnit.test('should handle comparisons when `customizer` returns `undefined`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isMatchWith({ 'a': __num_top__ }, { 'a': __num_top__ }, noop), __bool_top__);
    });
    QUnit.test('should not handle comparisons when `customizer` returns `true`', function (assert) {
        assert.expect(2);
        var customizer = function (value) {
            return _.isString(value) || undefined;
        };
        assert.strictEqual(_.isMatchWith([__str_top__], [__str_top__], customizer), __bool_top__);
        assert.strictEqual(_.isMatchWith({ '0': __str_top__ }, { '0': __str_top__ }, customizer), __bool_top__);
    });
    QUnit.test('should not handle comparisons when `customizer` returns `false`', function (assert) {
        assert.expect(2);
        var customizer = function (value) {
            return _.isString(value) ? __bool_top__ : undefined;
        };
        assert.strictEqual(_.isMatchWith([__str_top__], [__str_top__], customizer), __bool_top__);
        assert.strictEqual(_.isMatchWith({ '0': __str_top__ }, { '0': __str_top__ }, customizer), __bool_top__);
    });
    QUnit.test('should return a boolean value even when `customizer` does not', function (assert) {
        assert.expect(2);
        var object = { 'a': __num_top__ }, actual = _.isMatchWith(object, { 'a': __num_top__ }, stubA);
        assert.strictEqual(actual, __bool_top__);
        var expected = lodashStable.map(falsey, stubFalse);
        actual = [];
        lodashStable.each(falsey, function (value) {
            actual.push(_.isMatchWith(object, { 'a': __num_top__ }, lodashStable.constant(value)));
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should provide `stack` to `customizer`', function (assert) {
        assert.expect(1);
        var actual;
        _.isMatchWith({ 'a': __num_top__ }, { 'a': __num_top__ }, function () {
            actual = _.last(arguments);
        });
        assert.ok(isNpm ? actual.constructor.name == __str_top__ : actual instanceof mapCaches.Stack);
    });
    QUnit.test('should ensure `customizer` is a function', function (assert) {
        assert.expect(1);
        var object = { 'a': __num_top__ }, matches = _.partial(_.isMatchWith, object), actual = lodashStable.map([
                object,
                { 'a': __num_top__ }
            ], matches);
        assert.deepEqual(actual, [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should call `customizer` for values maps and sets', function (assert) {
        assert.expect(2);
        var value = { 'a': { 'b': __num_top__ } };
        if (Map) {
            var map1 = new Map();
            map1.set(__str_top__, value);
            var map2 = new Map();
            map2.set(__str_top__, value);
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
            if (pair[__num_top__]) {
                var argsList = [], array = lodashStable.toArray(pair[__num_top__]), object1 = { 'a': pair[__num_top__] }, object2 = { 'a': pair[__num_top__] };
                var expected = [
                    [
                        pair[__num_top__],
                        pair[__num_top__],
                        __str_top__,
                        object1,
                        object2
                    ],
                    [
                        array[__num_top__],
                        array[__num_top__],
                        __num_top__,
                        array,
                        array
                    ],
                    [
                        array[__num_top__][__num_top__],
                        array[__num_top__][__num_top__],
                        __num_top__,
                        array[__num_top__],
                        array[__num_top__]
                    ],
                    [
                        array[__num_top__][__num_top__],
                        array[__num_top__][__num_top__],
                        __num_top__,
                        array[__num_top__],
                        array[__num_top__]
                    ]
                ];
                if (index) {
                    expected.length = __num_top__;
                }
                _.isMatchWith({ 'a': pair[__num_top__] }, { 'a': pair[__num_top__] }, function () {
                    argsList.push(slice.call(arguments, __num_top__, -__num_top__));
                });
                assert.deepEqual(argsList, expected, index ? __str_top__ : __str_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
}());