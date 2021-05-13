QUnit.module('lodash.pullAll');
(function () {
    QUnit.test('should work with the same value for `array` and `values`', function (assert) {
        assert.expect(1);
        var array = [
                { 'a': __num_top__ },
                { 'b': 2 }
            ], actual = _.pullAll(array, array);
        assert.deepEqual(actual, []);
    });
}());