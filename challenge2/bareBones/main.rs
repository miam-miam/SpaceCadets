fn main() {
    let mut W: i32;
    let mut X: i32;
    let mut Y: i32;
    let mut Z: i32;
    X = 0;
    X += 1;
    X += 1;
    Y = 0;
    Y += 1;
    Y += 1;
    Y += 1;
    Z = 0;
    while X != 0 {
        W = 0;
        while Y != 0 {
            Z += 1;
            W += 1;
            Y -= 1;
        }
        while W != 0 {
            Y += 1;
            W -= 1;
        }
        X -= 1;
    }
}