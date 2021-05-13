QUnit.module('lodash.sample');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should return a random element', function (assert) {
        assert.expect(1);
        var actual = _.sample(array);
        assert.ok(lodashStable.includes(array, actual));
    });
    QUnit.test('should return `undefined` when sampling empty collections', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, noop);
        var actual = lodashStable.transform(empties, function (result, value) {
            try {
                result.push(_.sample(value));
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should sample an object', function (assert) {
        assert.expect(1);
        var object = {
                'a': 1,
                'b': 2,
                'c': __num_top__
            }, actual = _.sample(object);
        assert.ok(lodashStable.includes(array, actual));
    });
}());