QUnit.module('lodash.remove');
(function () {
    QUnit.test('should modify the array and return removed elements', function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3,
                4
            ], actual = _.remove(array, isEven);
        assert.deepEqual(array, [
            1,
            3
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            4
        ]);
    });
    QUnit.test('should provide correct `predicate` arguments', function (assert) {
        assert.expect(1);
        var argsList = [], array = [
                1,
                2,
                3
            ], clone = array.slice();
        _.remove(array, function (n, index) {
            var args = slice.call(arguments);
            args[2] = args[__num_top__].slice();
            argsList.push(args);
            return isEven(index);
        });
        assert.deepEqual(argsList, [
            [
                1,
                0,
                clone
            ],
            [
                2,
                1,
                clone
            ],
            [
                3,
                __num_top__,
                clone
            ]
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': 0,
                'b': 1
            },
            {
                'a': __num_top__,
                'b': 2
            }
        ];
        _.remove(objects, { 'a': 1 });
        assert.deepEqual(objects, [{
                'a': __num_top__,
                'b': 1
            }]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': 0,
                'b': __num_top__
            },
            {
                'a': 1,
                'b': 2
            }
        ];
        _.remove(objects, [
            'a',
            1
        ]);
        assert.deepEqual(objects, [{
                'a': 0,
                'b': 1
            }]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            { 'a': 0 },
            { 'a': 1 }
        ];
        _.remove(objects, 'a');
        assert.deepEqual(objects, [{ 'a': __num_top__ }]);
    });
    QUnit.test('should preserve holes in arrays', function (assert) {
        assert.expect(2);
        var array = [
            __num_top__,
            2,
            3,
            4
        ];
        delete array[1];
        delete array[3];
        _.remove(array, function (n) {
            return n === 1;
        });
        assert.notOk('0' in array);
        assert.notOk('2' in array);
    });
    QUnit.test('should treat holes as `undefined`', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        delete array[1];
        _.remove(array, function (n) {
            return n == null;
        });
        assert.deepEqual(array, [
            __num_top__,
            3
        ]);
    });
    QUnit.test('should not mutate the array until all elements to remove are determined', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            __num_top__
        ];
        _.remove(array, function (n, index) {
            return isEven(index);
        });
        assert.deepEqual(array, [2]);
    });
}());