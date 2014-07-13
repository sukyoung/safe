function Field(val){
    var v
    this.value = val;
}
Field.prototype = {
    get value(){
        return this.v;
    },
    set value(val){
        this.v = val;
    }
};
var field = new Field("test");
_<>_print(field.value)
field.value = "test2";
_<>_print(field.value)
