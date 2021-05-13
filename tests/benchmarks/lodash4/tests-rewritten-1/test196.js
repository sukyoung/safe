QUnit.module('lodash.pullAt');
(function () {
    QUnit.test('should modify the array and return removed elements', function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3
            ], actual = _.pullAt(array, [
                0,
                1
            ]);
        assert.deepEqual(array, [3]);
        assert.deepEqual(actual, [
            1,
            2
        ]);
    });
    QUnit.test('should work with unsorted indexes', function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12
            ], actual = _.pullAt(array, [
                1,
                3,
                11,
                7,
                5,
                9
            ]);
        assert.deepEqual(array, [
            1,
            3,
            5,
            7,
            9,
            11
        ]);
        assert.deepEqual(actual, [
            2,
            4,
            12,
            8,
            6,
            10
        ]);
    });
    QUnit.test('should work with repeated indexes', function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3,
                4
            ], actual = _.pullAt(array, [
                0,
                2,
                0,
                1,
                0,
                2
            ]);
        assert.deepEqual(array, [4]);
        assert.deepEqual(actual, [
            1,
            3,
            1,
            2,
            1,
            3
        ]);
    });
    QUnit.test('should use `undefined` for nonexistent indexes', function (assert) {
        assert.expect(2);
        var array = [
                'a',
                'b',
                'c'
            ], actual = _.pullAt(array, [
                2,
                4,
                0
            ]);
        assert.deepEqual(array, ['b']);
        assert.deepEqual(actual, [
            'c',
            undefined,
            'a'
        ]);
    });
    QUnit.test('should flatten `indexes`', function (assert) {
        assert.expect(4);
        var array = [
            'a',
            'b',
            'c'
        ];
        assert.deepEqual(_.pullAt(array, 2, 0), [
            'c',
            'a'
        ]);
        assert.deepEqual(array, ['b']);
        array = [
            'a',
            'b',
            'c',
            'd'
        ];
        assert.deepEqual(_.pullAt(array, [
            3,
            0
        ], 2), [
            'd',
            'a',
            'c'
        ]);
        assert.deepEqual(array, ['b']);
    });
    QUnit.test('should return an empty array when no indexes are given', function (assert) {
        assert.expect(4);
        var array = [
                'a',
                'b',
                'c'
            ], actual = _.pullAt(array);
        assert.deepEqual(array, [
            'a',
            'b',
            'c'
        ]);
        assert.deepEqual(actual, []);
        actual = _.pullAt(array, [], []);
        assert.deepEqual(array, [
            'a',
            'b',
            'c'
        ]);
        assert.deepEqual(actual, []);
    });
    QUnit.test('should work with non-index paths', function (assert) {
        assert.expect(2);
        var values = lodashStable.reject(empties, function (value) {
            return value === 0 || lodashStable.isArray(value);
        }).concat(-1, 1.1);
        var array = lodashStable.transform(values, function (result, value) {
            result[value] = 1;
        }, []);
        var expected = lodashStable.map(values, stubOne), actual = _.pullAt(array, values);
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(values, noop);
        actual = lodashStable.at(array, values);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var props = [
            -0,
            Object(-0),
            0,
            Object(0)
        ];
        var actual = lodashStable.map(props, function (key) {
            var array = [-1];
            array['-0'] = -2;
            return _.pullAt(array, key);
        });
        assert.deepEqual(actual, [
            [-__num_top__],
            [-2],
            [-1],
            [-1]
        ]);
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(3);
        var array = [];
        array.a = { 'b': 2 };
        var actual = _.pullAt(array, 'a.b');
        assert.deepEqual(actual, [2]);
        assert.deepEqual(array.a, {});
        try {
            actual = _.pullAt(array, 'a.b.c');
        } catch (e) {
        }
        assert.deepEqual(actual, [undefined]);
    });
    QUnit.test('should work with a falsey `array` when keys are given', function (assert) {
        assert.expect(1);
        var values = falsey.slice(), expected = lodashStable.map(values, lodashStable.constant(Array(4)));
        var actual = lodashStable.map(values, function (array) {
            try {
                return _.pullAt(array, 0, 1, 'pop', 'push');
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
}());