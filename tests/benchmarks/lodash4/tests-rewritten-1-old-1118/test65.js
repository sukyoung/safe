QUnit.module('lodash.flip');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should flip arguments provided to `func`', function (assert) {
        assert.expect(1);
        var flipped = _.flip(fn);
        assert.deepEqual(flipped('a', 'b', 'c', 'd'), [
            'd',
            __str_top__,
            'b',
            'a'
        ]);
    });
}());