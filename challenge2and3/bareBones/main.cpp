void multiply( int& X, int& Y, int& Total) { // tsd
    while (X != 0) { // d
        int W;
        W = 0; // ds
        while (Y != 0) {
            Total += 1;
            W += 1; // sdfaa
            Y -= 1;
        }
        while (W != 0) {
            Y += 1;
            W -= 1;
        }
        X -= 1;
    }
}

int main() {
    int nine;
    int three1;
    int three0;
    int test;
    nine = 0;
    three0 = 0;
    three0 += 1;
    three0 += 1;
    three0 += 1;
    three1 = 0; // l
    while (three0 != 0) {
        while (three0 != 0) {
            three0 -= 1;
        }
    }
    three1 += 1;
    three1 += 1;
    three1 += 1;
    test = 0;
    int $three0 = three0;int $$three0 = three0;multiply($three0, $$three0, nine); // op
    test += 1;
    test += 1;
}