fn multiply(mut X: i32, mut Y: i32, mut Total: i32) -> (i32, i32, i32) { // tsd
    while X != 0 {
        let mut W: i32; // d
        W = 0; // ds
        while Y != 0 {
            Total += 1;
            W += 1; // sdfaa
            Y -= 1;
        }
        while W != 0 {
            Y += 1;
            W -= 1;
        }
        X -= 1;
    }
    return (X, Y, Total);
}

fn main() {
    let mut nine: i32;
    let mut three1: i32;
    let mut three0: i32;
    let mut test: i32;
    nine = 0;
    three0 = 0;
    three0 += 1;
    three0 += 1;
    three0 += 1;
    three1 = 0; // l
    while three0 != 0 {
        while three0 != 0 {
            three0 -= 1;
        }
    }
    three1 += 1;
    three1 += 1;
    three1 += 1;
    test = 0;
    let (_, _, mut nine) = multiply(three0, three0, nine); // op
    test += 1;
    test += 1;
}