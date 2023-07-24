public void method() {
    int x = 0;
    {
        int z = 1;
        int y = z;
    }
    if (x == 1) {
        x = 2;
    }
    switch (x) {
        case 0 :
            x = 3;
            break;
        case 1 :
            {
                x = 1;
            }
            break;
        case 2 :
            {
                x = 1;
                break;
            }
        case 3 :
        default :
            System.identityHashCode(null);
            {
                x = 2;
            }
    }
}