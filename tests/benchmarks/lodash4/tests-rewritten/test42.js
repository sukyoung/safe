QUnit.module('lodash.defer');
(function () {
    QUnit.test('should defer `func` execution', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = __bool_top__;
        _.defer(function () {
            pass = __bool_top__;
        });
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, __num_top__);
    });
    QUnit.test('should provide additional arguments to `func`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var args;
        _.defer(function () {
            args = slice.call(arguments);
        }, __num_top__, __num_top__);
        setTimeout(function () {
            assert.deepEqual(args, [
                __num_top__,
                __num_top__
            ]);
            done();
        }, __num_top__);
    });
    QUnit.test('should be cancelable', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = __bool_top__, timerId = _.defer(function () {
                pass = __bool_top__;
            });
        clearTimeout(timerId);
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, __num_top__);
    });
}());