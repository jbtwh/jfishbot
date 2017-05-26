package jfishbot;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;

import java.util.List;

public interface User32 extends Library {
    public static class CURSORINFO extends Structure {

        public int cbSize;
        public int flags;
        public Pointer hCursor;
        public WinDef.POINT ptScreenPos;
        public static final List<String> FIELDS = createFieldsOrder(new String[]{"cbSize", "flags", "hCursor", "ptScreenPos"});

        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class ICONINFOEX extends WinGDI.ICONINFO {

        public int cbSize;
        public short wResID;
        public char[] szModName = new char[260];
        public char[] szResName = new char[260];
        public static final List<String> FIELDS = createFieldsOrder(new String[]{"cbSize", "fIcon", "xHotspot", "yHotspot", "hbmMask", "hbmColor", "wResID", "szModName", "szResName"});

        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static User32 INSTANCE = (User32) Native
            .loadLibrary("User32", User32.class);

    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_ARROW = 32512;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_IBEAM = 32513;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_WAIT = 32514;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_CROSS = 32515;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_UPARROW = 32516;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_SIZENWSE = 32642;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_SIZENESW = 32643;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_SIZEWE = 32644;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_SIZENS = 32645;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_SIZEALL = 32646;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_NO = 32648;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_HAND = 32649;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_APPSTARTING = 32650;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_HELP = 32651;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_ICON = 32641;
    /** @see #LoadCursorW(Pointer, int) */
    public static final int IDC_SIZE = 32640;

    /** @see #DrawIconEx(Pointer, int, int, Pointer, int, int, int, Pointer, int) */
    public static final int DI_COMPAT = 4;
    /** @see #DrawIconEx(Pointer, int, int, Pointer, int, int, int, Pointer, int) */
    public static final int DI_DEFAULTSIZE = 8;
    /** @see #DrawIconEx(Pointer, int, int, Pointer, int, int, int, Pointer, int) */
    public static final int DI_IMAGE = 2;
    /** @see #DrawIconEx(Pointer, int, int, Pointer, int, int, int, Pointer, int) */
    public static final int DI_MASK = 1;
    /** @see #DrawIconEx(Pointer, int, int, Pointer, int, int, int, Pointer, int) */
    public static final int DI_NORMAL = 3;
    /** @see #DrawIconEx(Pointer, int, int, Pointer, int, int, int, Pointer, int) */
    public static final int DI_APPBANDING = 1;

    /** http://msdn.microsoft.com/en-us/library/ms648391(VS.85).aspx */
    public Pointer LoadCursorW(Pointer hInstance,
                               int lpCursorName) throws LastErrorException;

    /** http://msdn.microsoft.com/en-us/library/ms648065(VS.85).aspx */
    public boolean DrawIconEx(Pointer hdc, int xLeft,
                              int yTop, Pointer hIcon, int cxWidth, int cyWidth,
                              int istepIfAniCur, Pointer hbrFlickerFreeDraw,
                              int diFlags) throws LastErrorException;

    /** https://msdn.microsoft.com/en-us/library/ms648064(v=vs.85).aspx */
    public boolean DrawIcon(Pointer hdc, int x,
                            int y, Pointer hIcon) throws LastErrorException;

    /** https://msdn.microsoft.com/en-us/library/ms648389(v=vs.85).aspx */
    public boolean GetCursorInfo(CURSORINFO pci) throws LastErrorException;

    /** https://msdn.microsoft.com/en-us/library/windows/desktop/ms648388(v=vs.85).aspx */
    public Pointer GetCursor() throws LastErrorException;

    public boolean SetCursorPos(int x, int y) throws LastErrorException;

    public boolean GetCursorPos(WinDef.POINT point) throws LastErrorException;

    public boolean ShowCursor(boolean show) throws LastErrorException;

    public boolean GetIconInfoExW(Pointer hIcon, ICONINFOEX iconinfoex) throws LastErrorException;

    public boolean GetIconInfo(Pointer hIcon, WinGDI.ICONINFO iconinfo) throws LastErrorException;
}