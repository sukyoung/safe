QUnit.module('lodash.pullAll');
(function () {
    QUnit.test('should work with the same value for `array` and `values`', function (assert) {
        assert.expect(1);
        var array = [
                { 'a': 1 },
                { 'b': __num_top__ }
            ], actual = _.pullAll(array, array);
        assert.deepEqual(actual, []);
    });
}());