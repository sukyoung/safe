QUnit.module('lodash.flip');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should flip arguments provided to `func`', function (assert) {
        assert.expect(1);
        var flipped = _.flip(fn);
        assert.deepEqual(flipped(__str_top__, __str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
}());