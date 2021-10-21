clear X;
clear X;
incr X;
incr X; // Hoi
clear Y;
incr Y; // Test
incr Y;
incr Y;
clear Z;
while X not 0 do;
    clear W;
    while Y not 0 do;
        incr Z;
        incr W;
        decr Y;
    end;
    while Z not 0 do;
        incr Y;
        decr W;
    end;
    decr X;
end;
