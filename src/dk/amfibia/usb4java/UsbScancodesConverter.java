package dk.amfibia.usb4java;


public class UsbScancodesConverter {

    //USB HID Keyboard Scancode = Page(0x07)
//Document:Universal Serial Bus HID Usage Tables Page[53..60]
    private static final byte BANG = 0x1E; // 1 !
    private static final byte AT = 0x1F; // 2 @
    private static final byte POUND = 0x20; // 3 #
    private static final byte DOLLAR = 0x21; // 4 $
    private static final byte PERCENT = 0x22; // 5 %
    private static final byte CAP = 0x23; // 6 ^
    private static final byte AND = 0x24; // 7 &
    private static final byte STAR = 0x25; // 8 *
    private static final byte OPENBKT = 0x26; // 9 (
    private static final byte CLOSEBKT = 0x27; // 0 )
    private static final byte RETURN = 0x28; // Enter
    private static final byte ESCAPE = 0x29; // Esc
    private static final byte BACKSPACE = 0x2A; // Backspace
    private static final byte TAB = 0x2B; // Tab
    private static final byte SPACE = 0x2C; // Space bar
    private static final byte HYPHEN = 0x2D; // - _
    private static final byte EQUAL = 0x2E; // = +
    private static final byte SQBKTOPEN = 0x2F; // [ {
    private static final byte SQBKTCLOSE = 0x30; // ] }
    private static final byte BACKSLASH = 0x31; // \ |
    private static final byte SEMICOLON = 0x33; // ; :
    private static final byte INVCOMMA = 0x34; // ' "
    private static final byte TILDE = 0x35; // ~
    private static final byte COMMA = 0x36; // , <
    private static final byte PERIOD = 0x37; // . >
    private static final byte FRONTSLASH = 0x38; // / ?
    private static final byte DELETE = 0x4c; //
    // Sticky keys
    private static final byte CAPSLOCK = 0x39; // Cap Lock
    private static final byte SCROLLLOCK = 0x47; // Scroll Lock
    private static final byte NUMLOCK = 0x53; // Num Lock

    /* Modifier masks. One for both modifiers */
    private static final byte CTRL = 0x11; // Ctrl
    private static final byte SHIFT = 0x22; // Shift
    private static final byte ALT = 0x44; // Alt
    private static final byte GUI = (byte)0x88; // GUI

    /* Sticky keys output report bitmasks */
// Output Report = 1 Byte
// ->Bit[0] = NUM LOCK
// ->Bit[1] = CAPS LOCK
// ->Bit[2] = SCROLL LOCK
// ->Bit[3] = COMPOSE
// ->Bit[4] = KANA
// ->Bit[5..7] = CONSTANT
    static byte bmNUMLOCK = 0x01;
    static byte bmCAPSLOCK = 0x02;
    static byte bmSCROLLLOCK = 0x04;

    /* Sticky key state */
    static boolean numLock = false;
    static boolean capsLock = false;
    static boolean scrollLock = false;

    /* HID to ASCII converter. Takes HID keyboard scancode, returns ASCII code */
    public static byte HIDtoA(byte HIDbyte) {
        return HIDtoA(HIDbyte, (byte)0);
    }

    public static byte HIDtoA(byte HIDbyte, byte mod) {
        if (HIDbyte == RETURN) return 0x0a;
/* upper row of the keyboard, numbers and special symbols */
        if (HIDbyte >= 0x1e && HIDbyte <= 0x27) { // 1..9,0
            if((mod & SHIFT) != 0 || numLock ) { //shift key pressed
                switch (HIDbyte) {
                    case BANG: return( 0x21 ); // 1 = !
                    case AT: return( 0x40 ); // 2 = @
                    case POUND: return( 0x23 ); // 3 = #
                    case DOLLAR: return( 0x24 ); // 4 = $
                    case PERCENT: return( 0x25 ); // 5 = %
                    case CAP: return( 0x5e ); // 6 = ^
                    case AND: return( 0x26 ); // 7 = &
                    case STAR: return( 0x2a ); // 8 = *
                    case OPENBKT: return( 0x28 ); // 9 = (
                    case CLOSEBKT: return( 0x29 ); // 0 = )
                }//switch( HIDbyte...
            }
// 1..9,0
            else
            {
                if( HIDbyte == 0x27 )
                {
                    return( 0x30 ); // 0
                }
                else
                {
                    return (byte) (HIDbyte + 0x13); // 0x1E..0x26 = 0x31(1)..0x39(9)
                }
            }//numbers
        }//if( HIDbyte >= 0x1e && HIDbyte <= 0x27
/**/
/* number pad. Arrows are not supported */
//End(1) Down(2) PageDown(3) Left(4) 5 Right(6) Home(7) Up(8) PageUp(9)
        if(( HIDbyte >= 0x59 && HIDbyte <= 0x61 ) && (numLock)) { // numbers 1-9
            return (byte) (HIDbyte - 0x28); // 0x59..0x61 = 0x31(1)..0x39(9)
        }
//Insert(0)
        if(( HIDbyte == 0x62 ) && (numLock))
        {
            return( 0x30 ); // 0
        }
/* Letters a-z */
        if( HIDbyte >= 0x04 && HIDbyte <= 0x1d )
        {
            if(((capsLock) && ( mod & SHIFT ) == 0 ) ||
                    ((!capsLock) && (mod & SHIFT)!=0))
            {
                return (byte) (HIDbyte + 0x3d); // A..Z
            }
            else
            {
                return (byte) (HIDbyte + 0x5d); // a..z
            }
        }//if( HIDbyte >= 0x04 && HIDbyte <= 0x1d...
/* Other special symbols */
//Spacebar -(_) =(+) [({) ])}) \(|) # ;(:) '(") ~ ,(") .(>) /(?)
        if( HIDbyte >= 0x2c && HIDbyte <= 0x38 )
        {
            switch( HIDbyte )
            {
                case SPACE: return( 0x20 ); // Space Bar
                case HYPHEN: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x2d ); // -
                }
                else
                {
                    return( 0x5f ); // _
                }
                case EQUAL: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x3d ); // =
                }
                else
                {
                    return( 0x2b ); // +
                }
                case SQBKTOPEN: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x5b ); // [
                }
                else
                {
                    return( 0x7b ); // {
                }
                case SQBKTCLOSE: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x5d ); // ]
                }
                else
                {
                    return( 0x7d ); // }
                }
                case BACKSLASH: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x5c ); // ]
                }
                else
                {
                    return( 0x7c ); // }
                }
                case SEMICOLON: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x3b ); // ;
                }
                else
                {
                    return( 0x3a ); // :
                }
                case INVCOMMA: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x27 ); // ,
                }
                else
                {
                    return( 0x22 ); // "
                }
                case TILDE: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x60 ); // `
                }
                else
                {
                    return( 0x7e ); // ~
                }
                case COMMA: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x2c ); // ,
                }
                else
                {
                    return( 0x3c ); // <
                }
                case PERIOD: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x2e ); // .
                }
                else
                {
                    return( 0x3e ); // >
                }
                case FRONTSLASH: if(( mod & SHIFT ) == 0 )
                {
                    return( 0x2f ); // /
                }
                else
                {
                    return( 0x3f ); // ?
                }
                default: break;
            }//switch( HIDbyte..
        }//if( HIDbye >= 0x2d && HIDbyte <= 0x38..
        return( 0 );
    }



}
