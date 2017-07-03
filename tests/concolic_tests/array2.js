function invertMatrix(self) {
    var temp = new Array(16);
    var tx = -self[3];
    var ty = -self[7];
    var tz = -self[11];
    for (h = 0; h < 3; h++) 
        for (v = 0; v < 3; v++) 
            temp[h + v * 4] = self[v + h * 4];
    for (i = 0; i < 11; i++)
        self[i] = temp[i];
    self[3] = tx * self[0] + ty * self[1] + tz * self[2];
    self[7] = tx * self[4] + ty * self[5] + tz * self[6];
    self[11] = tx * self[8] + ty * self[9] + tz * self[10];
    return self;
}

var m = new Array(16);
invertMatrix(m);
